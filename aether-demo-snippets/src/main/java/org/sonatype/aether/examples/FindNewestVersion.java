/*******************************************************************************
 * Copyright (c) 2010, 2012 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.sonatype.aether.examples;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.examples.util.Booter;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.VersionRangeRequest;
import org.sonatype.aether.resolution.VersionRangeResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.version.Version;

/**
 * Determines the newest version of an artifact.
 */
public class FindNewestVersion
{

    public static void main( String[] args )
        throws Exception
    {
        System.out.println( "------------------------------------------------------------" );
        System.out.println( FindNewestVersion.class.getSimpleName() );

        RepositorySystem system = Booter.newRepositorySystem();

        RepositorySystemSession session = Booter.newRepositorySystemSession( system );

        Artifact artifact = new DefaultArtifact( "org.sonatype.aether:aether-util:[0,)" );

        RemoteRepository repo = Booter.newCentralRepository();

        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact( artifact );
        rangeRequest.addRepository( repo );

        VersionRangeResult rangeResult = system.resolveVersionRange( session, rangeRequest );

        Version newestVersion = rangeResult.getHighestVersion();

        System.out.println( "Newest version " + newestVersion + " from repository "
            + rangeResult.getRepository( newestVersion ) );
    }

}
