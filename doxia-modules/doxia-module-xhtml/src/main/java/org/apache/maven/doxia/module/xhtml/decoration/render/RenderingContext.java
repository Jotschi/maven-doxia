package org.apache.maven.doxia.module.xhtml.decoration.render;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.codehaus.plexus.util.PathTool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @since 1.0
 */
public class RenderingContext
{
    private final File basedir;

    private final String inputName;

    private final String outputName;

    private final String parserId;

    private final String relativePath;

    private Map attributes;

    public RenderingContext( File basedir, String document )
    {
        this( basedir, document, null );
    }

    public RenderingContext( File basedir, String document, String parserId )
    {
        this.basedir = basedir;

        this.outputName = document.substring( 0, document.indexOf( "." ) ).replace( '\\', '/' ) + ".html";

        this.relativePath = PathTool.getRelativePath( basedir.getPath(), new File( basedir, document ).getPath() );

        this.inputName = document;

        this.parserId = parserId;

        this.attributes = new HashMap();
    }

    public File getBasedir()
    {
        return basedir;
    }

    public String getInputName()
    {
        return inputName;
    }

    public String getOutputName()
    {
        return outputName;
    }

    public String getParserId()
    {
        return parserId;
    }

    public String getRelativePath()
    {
        return relativePath;
    }

    public void setAttribute( String key, String value )
    {
        attributes.put( key, value );
    }

    public String getAttribute( String key )
    {
        return (String) attributes.get( key );
    }
}
