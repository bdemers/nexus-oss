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
 * Registers all feature groups for coreui.
 *
 * @since 3.0
 */
Ext.define('NX.coreui.controller.FeatureGroups', {
  extend: 'Ext.app.Controller',

  /**
   * @override
   */
  init: function () {
    var me = this;

    me.getApplication().getIconController().addIcons({
      'feature-repository': {
        file: 'database.png',
        variants: ['x16', 'x32']
      },
      'feature-security': {
        file: 'security.png',
        variants: ['x16', 'x32']
      },
      'feature-support': {
        file: 'support.png',
        variants: ['x16', 'x32']
      },
      'feature-system': {
        file: 'cog.png',
        variants: ['x16', 'x32']
      }
    });

    me.getApplication().getFeaturesController().registerFeature([
      {
        mode: 'admin',
        path: '/Repository',
        group: true,
        weight: 50
      },
      {
        mode: 'admin',
        path: '/Security',
        group: true,
        weight: 90
      },
      {
        mode: 'admin',
        path: '/Support',
        group: true
      },
      {
        mode: 'admin',
        path: '/System',
        group: true,
        weight: 1000
      }
    ]);
  }

});