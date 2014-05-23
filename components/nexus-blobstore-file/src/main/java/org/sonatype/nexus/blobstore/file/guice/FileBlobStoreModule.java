/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2014 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.blobstore.file.guice;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.blobstore.api.BlobStore;
import org.sonatype.nexus.blobstore.file.BlobMetadataStore;
import org.sonatype.nexus.blobstore.file.FileBlobStore;
import org.sonatype.nexus.blobstore.file.FileOperations;
import org.sonatype.nexus.blobstore.file.FilePathPolicy;
import org.sonatype.nexus.blobstore.file.HashingSubdirFileLocationPolicy;
import org.sonatype.nexus.blobstore.file.SimpleFileOperations;
import org.sonatype.nexus.blobstore.file.kazuki.KazukiBlobMetadataStore;
import org.sonatype.nexus.configuration.application.ApplicationDirectories;

import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import io.kazuki.v0.store.easy.EasyKeyValueStoreModule;
import io.kazuki.v0.store.index.SecondaryIndexStore;
import io.kazuki.v0.store.jdbi.JdbiDataSourceConfiguration;
import io.kazuki.v0.store.keyvalue.KeyValueStore;
import io.kazuki.v0.store.keyvalue.KeyValueStoreConfiguration;
import io.kazuki.v0.store.lifecycle.Lifecycle;
import io.kazuki.v0.store.lifecycle.LifecycleModule;
import io.kazuki.v0.store.schema.SchemaStore;
import io.kazuki.v0.store.sequence.SequenceServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.blobstore.file.kazuki.KazukiBlobMetadataStore.METADATA_TYPE;

/**
 * @since 3.0
 */
public class FileBlobStoreModule
    extends PrivateModule
{
  private final String name;

  private static final String KZ_DB_NAME = "fileblobstore-kazukidb-name";

  private static final String KZ_DB_LOCATION = "fileblobstore-kazukidb-location";

  private final Logger log = LoggerFactory.getLogger(FileBlobStoreModule.class);

  @Inject
  public FileBlobStoreModule(final String name) {
    this.name = checkNotNull(name, "name");
  }

  @Override
  protected void configure() {
    bind(FileOperations.class).to(SimpleFileOperations.class).in(Scopes.SINGLETON);

    bind(JdbiDataSourceConfiguration.class).annotatedWith(Names.named(name)).toProvider(
        JdbiDsConfigProvider.class);

    bindKazukiBlobMetadataStoreDependency(Lifecycle.class);
    bindKazukiBlobMetadataStoreDependency(KeyValueStore.class);
    bindKazukiBlobMetadataStoreDependency(SchemaStore.class);
    bindKazukiBlobMetadataStoreDependency(SecondaryIndexStore.class);

    bind(BlobMetadataStore.class).to(KazukiBlobMetadataStore.class).in(
        Scopes.SINGLETON);
    bind(org.sonatype.sisu.goodies.lifecycle.Lifecycle.class).annotatedWith(Names.named(name))
        .to(KazukiBlobMetadataStore.class);
    expose(org.sonatype.sisu.goodies.lifecycle.Lifecycle.class).annotatedWith(Names.named(name));

    bind(BlobStore.class).annotatedWith(Names.named(name)).to(FileBlobStore.class);
    expose(BlobStore.class).annotatedWith(Names.named(name));

    // Kazuki lifecycle management
    install(new LifecycleModule(name));

    // Kazuki key-value store
    install(new EasyKeyValueStoreModule(name, null)
        .withSequenceConfig(getSequenceServiceConfiguration())
        .withKeyValueStoreConfig(getKeyValueStoreConfiguration()));
  }

  /**
   * The {@link KazukiBlobMetadataStore} parameters need to be associated with the specific kazuki store's name.
   */
  @SuppressWarnings("unchecked")
  private void bindKazukiBlobMetadataStoreDependency(final Class type) {
    bind(type).annotatedWith(Names.named(KazukiBlobMetadataStore.DYNAMIC_KZ_NAME))
        .to(Key.get(type, Names.named(name)));
  }

  private SequenceServiceConfiguration getSequenceServiceConfiguration() {
    SequenceServiceConfiguration.Builder builder = new SequenceServiceConfiguration.Builder();

    builder.withDbType("h2");
    builder.withGroupName("nexus");
    builder.withStoreName(name);
    builder.withStrictTypeCreation(true);

    return builder.build();
  }

  private KeyValueStoreConfiguration getKeyValueStoreConfiguration() {
    KeyValueStoreConfiguration.Builder builder = new KeyValueStoreConfiguration.Builder();

    builder.withDbType("h2");
    builder.withGroupName("nexus");
    builder.withStoreName(name);
    builder.withPartitionName("default");
    builder.withPartitionSize(100_000L); // TODO: Confirm this is sensible
    builder.withStrictTypeCreation(true);
    builder.withDataType(METADATA_TYPE);
    builder.withSecondaryIndex(true);

    return builder.build();
  }

  @Provides
  FilePathPolicy provideFilePathPolicy(final ApplicationDirectories applicationDirectories) {
    final File workDirectory = applicationDirectories.getWorkDirectory("fileblobstore/" + name + "/content");
    return new HashingSubdirFileLocationPolicy(workDirectory.toPath());
  }

  @Provides
  @Named(KZ_DB_LOCATION)
  File provideKazukiDatabaseLocation(final ApplicationDirectories applicationDirectories) {
    File dir = applicationDirectories.getWorkDirectory("fileblobstore/" + name + "/db");
    log.info("File blob store {} metadata stored in Kazuki db: {}", name, dir);
    File file = new File(dir, dir.getName());
    return file.getAbsoluteFile();
  }

  /**
   * Using a provider class here is necessary so that the provider can be bound to an arbitrary {@code @Name}.
   */
  static class JdbiDsConfigProvider
      implements Provider<JdbiDataSourceConfiguration>
  {
    private final File kazukiDatabaseLocation;

    @Inject
    JdbiDsConfigProvider(@Named(KZ_DB_LOCATION) final File kazukiDatabaseLocation) {
      this.kazukiDatabaseLocation = kazukiDatabaseLocation;
    }

    @Override
    public JdbiDataSourceConfiguration get() {
      JdbiDataSourceConfiguration.Builder builder = new JdbiDataSourceConfiguration.Builder();

      builder.withJdbcDriver("org.h2.Driver");
      builder.withJdbcUrl("jdbc:h2:" + kazukiDatabaseLocation);
      builder.withJdbcUser("root");
      builder.withJdbcPassword("not_really_used");
      builder.withPoolMinConnections(25);
      builder.withPoolMaxConnections(25);

      return builder.build();
    }
  }
}
