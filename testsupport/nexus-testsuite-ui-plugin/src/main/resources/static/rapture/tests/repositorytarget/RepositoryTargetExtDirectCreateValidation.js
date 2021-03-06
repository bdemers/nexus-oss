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
/**
 * Tests Repository Target Ext.Direct create() validation.
 *
 * @since 2.8
 */
StartTest(function (t) {

  var async = t.beginAsync(),
      name = 'Test-' + new Date().getTime();

  Ext.Direct.addProvider(NX.direct.api.REMOTING_API);

  NX.direct.coreui_RepositoryTarget.create(
      {
      },
      function (response) {
        var success = Ext.isDefined(response) && response.success;
        t.endAsync(async);
        t.ok(!success, 'create() failed');
        if (!success) {
          t.ok(response.errors, 'create() returned errors');
          t.ok(response.errors['name'], 'name was validated');
        }
      }
  );

});
