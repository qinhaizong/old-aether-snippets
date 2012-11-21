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
package org.sonatype.aether.examples.aether;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.deployment.DeployRequest;
import org.sonatype.aether.deployment.DeploymentException;
import org.sonatype.aether.examples.util.Booter;
import org.sonatype.aether.examples.util.ConsoleDependencyGraphDumper;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.installation.InstallRequest;
import org.sonatype.aether.installation.InstallationException;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

public class Aether
{
    private final String remoteRepository;

    private final RepositorySystem repositorySystem;

    private final LocalRepository localRepository;

    public Aether( final String remoteRepository, final String localRepository )
    {
        this.remoteRepository = remoteRepository;
        this.repositorySystem = Booter.newRepositorySystem();
        this.localRepository = new LocalRepository( localRepository );
    }

    private RepositorySystemSession newSession()
    {
        final DefaultRepositorySystemSession session = Booter.newRepositorySystemSession( repositorySystem );
        session.setLocalRepositoryManager( repositorySystem.newLocalRepositoryManager( localRepository ) );
        return session;
    }

    public AetherResult resolve( final String groupId, final String artifactId, final String version )
        throws DependencyResolutionException
    {
        final RepositorySystemSession session = newSession();
        final Dependency dependency =
            new Dependency( new DefaultArtifact( groupId, artifactId, "", "jar", version ), "runtime" );
        final RemoteRepository central = new RemoteRepository( "central", "default", remoteRepository );

        final CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot( dependency );
        collectRequest.addRepository( central );

        final DependencyRequest dependencyRequest = new DependencyRequest();
        dependencyRequest.setCollectRequest( collectRequest );

        final DependencyNode rootNode = repositorySystem.resolveDependencies( session, dependencyRequest )
                                                        .getRoot();

        final StringBuilder dump = new StringBuilder();
        displayTree( rootNode, dump );

        final PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
        rootNode.accept( nlg );

        return new AetherResult( rootNode, nlg.getFiles(), nlg.getClassPath() );
    }

    public void install( final Artifact artifact, final Artifact pom )
        throws InstallationException
    {
        final RepositorySystemSession session = newSession();

        final InstallRequest installRequest = new InstallRequest();
        installRequest.addArtifact( artifact )
                      .addArtifact( pom );

        repositorySystem.install( session, installRequest );
    }

    public void deploy( final Artifact artifact, final Artifact pom, final String remoteRepository )
        throws DeploymentException
    {
        final RepositorySystemSession session = newSession();

        final Authentication auth = new Authentication( "admin", "admin123" );
        final RemoteRepository nexus =
            new RemoteRepository( "nexus", "default", remoteRepository ).setAuthentication( auth );

        final DeployRequest deployRequest = new DeployRequest();
        deployRequest.addArtifact( artifact )
                     .addArtifact( pom );
        deployRequest.setRepository( nexus );

        repositorySystem.deploy( session, deployRequest );
    }

    private void displayTree( final DependencyNode node, final StringBuilder sb )
    {
        final ByteArrayOutputStream os = new ByteArrayOutputStream( 1024 );
        node.accept( new ConsoleDependencyGraphDumper( new PrintStream( os ) ) );
        sb.append( os.toString() );
    }

}
