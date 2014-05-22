package org.sonatype.nexus.blobstore.file;

import java.io.ByteArrayInputStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.blobstore.api.BlobStore;
import org.sonatype.nexus.blobstore.file.guice.FileBlobStoreModule;
import org.sonatype.sisu.goodies.lifecycle.Lifecycle;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * @since 3.0
 */
public class FileBlobStoreModuleTest
{
  @Inject
  @Named("alpha")
  private BlobStore blobStoreA;

  @Inject
  @Named("beta")
  private BlobStore blobStoreB;

  @Inject
  @Named("alpha")
  private Lifecycle lifecycleA;

  @Inject
  @Named("beta")
  private Lifecycle lifecycleB;

  @Before
  public void instantiateTwoFileBlobStores() throws Exception {
    final Injector injector = Guice
        .createInjector(new FileBlobStoreModule("alpha"), new FileBlobStoreModule("beta"), new TempDirectoryModule());

    injector.injectMembers(this);

    assertThat(blobStoreA, is(not(nullValue())));
    assertThat(blobStoreB, is(not(nullValue())));

    lifecycleA.start();
    lifecycleB.start();
  }

  @After
  public void shutDownBlobStores() throws Exception {
    if (lifecycleA != null) {
      lifecycleA.stop();
    }
    if (lifecycleB != null) {
      lifecycleB.stop();
    }
  }

  @Test
  public void createABlob() {
    blobStoreA.create(new ByteArrayInputStream(new byte[100]),
        ImmutableMap.of(BlobStore.BLOB_NAME_HEADER, "name", BlobStore.CREATED_BY_HEADER, "michael"));
    blobStoreB.create(new ByteArrayInputStream(new byte[100]),
        ImmutableMap.of(BlobStore.BLOB_NAME_HEADER, "name", BlobStore.CREATED_BY_HEADER, "michael"));
  }


}
