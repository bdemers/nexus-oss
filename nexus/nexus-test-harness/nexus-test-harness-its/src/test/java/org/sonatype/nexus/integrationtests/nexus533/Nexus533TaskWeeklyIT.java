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
package org.sonatype.nexus.integrationtests.nexus533;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.sonatype.nexus.rest.model.ScheduledServicePropertyResource;
import org.sonatype.nexus.rest.model.ScheduledServiceWeeklyResource;
import org.sonatype.nexus.tasks.descriptors.UpdateIndexTaskDescriptor;

public class Nexus533TaskWeeklyIT
    extends AbstractNexusTasksIntegrationIT<ScheduledServiceWeeklyResource>
{

    private static ScheduledServiceWeeklyResource scheduledTask;

    @Override
    public ScheduledServiceWeeklyResource getTaskScheduled()
    {
        if ( scheduledTask == null )
        {
            scheduledTask = new ScheduledServiceWeeklyResource();
            scheduledTask.setEnabled( true );
            scheduledTask.setId( null );
            scheduledTask.setName( "taskWeekly" );
            scheduledTask.setSchedule( "weekly" );
            // A future date
            Date startDate = DateUtils.addDays( new Date(), 10 );
            startDate = DateUtils.round( startDate, Calendar.DAY_OF_MONTH );
            scheduledTask.setStartDate( String.valueOf( startDate.getTime() ) );
            scheduledTask.setRecurringTime( "03:30" );
            scheduledTask.setRecurringDay( Arrays.asList( new String[] { "monday", "wednesday", "friday" } ) );

            scheduledTask.setTypeId( UpdateIndexTaskDescriptor.ID );

            ScheduledServicePropertyResource prop = new ScheduledServicePropertyResource();
            prop.setKey( "repositoryId" );
            prop.setValue( "all_repo" );
            scheduledTask.addProperty( prop );
        }
        return scheduledTask;
    }

    @Override
    public void updateTask( ScheduledServiceWeeklyResource scheduledTask )
    {
        scheduledTask.setRecurringTime( "00:00" );
    }

}
