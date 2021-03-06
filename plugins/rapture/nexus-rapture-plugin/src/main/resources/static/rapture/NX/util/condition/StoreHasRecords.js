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
 * A {@link NX.util.condition.Condition} that is satisfied when specified store has records.
 *
 * @since 3.0
 */
Ext.define('NX.util.condition.StoreHasRecords', {
  extend: 'NX.util.condition.Condition',

  /**
   * @cfg {String} Id of store to be monitored
   */
  store: undefined,

  bind: function () {
    var me = this,
        store;

    if (!me.bounded) {
      store = NX.getApplication().getStore(me.store);
      me.mon(store, {
        datachanged: me.evaluate,
        beforeload: Ext.pass(me.evaluate, [undefined]),
        scope: me
      });
      me.callParent();
      me.evaluate(store);
    }

    return me;
  },

  evaluate: function (store) {
    var me = this;

    if (me.bounded) {
      me.setSatisfied(Ext.isDefined(store) && (store.getCount() > 0));
    }
  },

  toString: function () {
    var me = this;
    return me.self.getName() + '{ store=' + me.store + ' }';
  }

});