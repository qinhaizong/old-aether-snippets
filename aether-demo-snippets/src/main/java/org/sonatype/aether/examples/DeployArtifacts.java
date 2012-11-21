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

import java.io.File;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.deployment.DeployRequest;
import org.sonatype.aether.examples.util.Booter;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.SubArtifact;

/**
 * Deploys a JAR and its POM to a remote repository.
 */
public class DeployArtifacts
{

    public static void main( final String[] args )
        throws Exception
    {
        System.out.println( "------------------------------------------------------------" );
        System.out.println( DeployArtifacts.class.getSimpleName() );

        final RepositorySystem system = Booter.newRepositorySystem();

        final RepositorySystemSession session = Booter.newRepositorySystemSession( system );

        Artifact jarArtifact = new DefaultArtifact( "test", "org.sonatype.aether.examples", "", "jar", "0.1-SNAPSHOT" );
        jarArtifact = jarArtifact.setFile( new File( "src/main/data/demo.jar" ) );

        Artifact pomArtifact = new SubArtifact( jarArtifact, "", "pom" );
        pomArtifact = pomArtifact.setFile( new File( "pom.xml" ) );

        final RemoteRepository distRepo =
            new RemoteRepository( "org.sonatype.aether.examples", "default", new File( "target/dist-repo" ).toURI()
                                                                                                           .toString() );

        final DeployRequest deployRequest = new DeployRequest();
        deployRequest.addArtifact( jarArtifact )
                     .addArtifact( pomArtifact );
        deployRequest.setRepository( distRepo );

        system.deploy( session, deployRequest );
    }

}
