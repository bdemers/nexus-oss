/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.mock.pages;

import org.sonatype.nexus.mock.components.Button;
import org.sonatype.nexus.mock.components.Component;
import org.sonatype.nexus.mock.components.Grid;
import org.sonatype.nexus.mock.components.Menu;

import com.thoughtworks.selenium.Selenium;

public class AbstractTab
    extends Component
{

    protected Grid grid;

    protected Button refreshButton;

    protected Button addButton;

    protected Button deleteButton;

    protected Menu addMenu;

    public AbstractTab( Selenium selenium, String expression )
    {
        super( selenium, expression );

        this.grid = new Grid( selenium, expression + ".gridPanel" );

        this.refreshButton = new Button( selenium, expression + ".refreshButton" );
        this.deleteButton = new Button( selenium, expression + ".toolbarDeleteButton" );

        this.addButton = new Button( selenium, expression + ".toolbarAddButton" );
        this.addMenu = new Menu( selenium, addButton.getExpression() + ".menu" );
    }

    public MessageBox delete()
    {
        this.deleteButton.click();

        return new MessageBox( selenium );
    }

    public void refresh()
    {
        this.refreshButton.click();

        this.grid.waitToLoad();
    }

    public Grid getGrid()
    {
        return grid;
    }

    public Button getRefreshButton()
    {
        return refreshButton;
    }

    public Button getAddButton()
    {
        return addButton;
    }

    public Button getDeleteButton()
    {
        return deleteButton;
    }

    public Menu getAddMenu()
    {
        return addMenu;
    }

}
