/*
 *  Copyright 2005 Zauber <info /at/ zauber dot com dot ar>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.maven.doxia.module.twiki.parser;

import java.lang.reflect.Method;
import java.util.Collections;

import org.apache.maven.doxia.sink.Sink;


/**
 * Block that represents a section
 *
 * @author Juan F. Codagnone
 * @since Nov 1, 2005
 */
public class SectionBlock extends AbstractFatherBlock
{
    /**
     * @see #SectionBlock(String, int, Block[])
     */
    private final String title;
    /**
     * @see #SectionBlock(String, int, Block[])
     */
    private final int level;

    /**
     * Creates the SectionBlock.
     * <p/>
     * No parameter can be <code>null</code>
     *
     * @param title  the section title.
     * @param level  the section level: 0 < level < 6
     * @param blocks child blocks
     * @throws IllegalArgumentException if the parameters are not in the domain
     */
    public SectionBlock( final String title, final int level,
                         final Block []blocks ) throws IllegalArgumentException
    {
        super( blocks );
        final int maxLevel = 5;
        if ( title == null )
        {
            throw new IllegalArgumentException( "title cant be null" );
        }
        else if ( level < 1 || level > maxLevel )
        {
            throw new IllegalArgumentException( "invalid level: " + level );
        }

        this.title = title;
        this.level = level;
    }

    /**
     * @see AbstractFatherBlock#before(org.apache.maven.doxia.sink.Sink)
     */
    @Override
    public final void before( final Sink sink )
    {
        sectionStart( sink );
        sink.sectionTitle();
        sink.text( title );
        sink.sectionTitle_();

    }

    /**
     * @see AbstractFatherBlock#after(org.apache.maven.doxia.sink.Sink)
     */
    @Override
    public final void after( final Sink sink )
    {
        sectionEnd( sink );

    }

    /**
     * call to sink.section<Level>()
     *
     * @param sink sink
     */
    private void sectionStart( final Sink sink )
    {
        invokeVoidVoid( sink, "section" + level );
    }

    /**
     * call to sink.section<Level>_()
     *
     * @param sink sink
     */
    private void sectionEnd( final Sink sink )
    {
        invokeVoidVoid( sink, "section" + level + "_" );
    }


    /**
     * Let you call sink's methods that returns <code>null</code> and have
     * no parameters.
     *
     * @param sink the Sink
     * @param name the name of the method to call
     * @throws IllegalArgumentException on error
     */
    private void invokeVoidVoid( final Sink sink, final String name )
        throws IllegalArgumentException
    {
        try
        {
            final Method m = sink.getClass().getMethod( name, new Class[]{} );
            m.invoke( sink, Collections.EMPTY_LIST.toArray() );
        }
        catch ( Exception e )
        {
            // TODO tirar una mejor exception
            throw new IllegalArgumentException( "invoking sink's " + name
                + " method", e );
        }
    }

    /**
     * Returns the level.
     *
     * @return <code>int</code> with the level.
     */
    public final int getLevel()
    {
        return level;
    }

    /**
     * Returns the title.
     *
     * @return <code>String</code> with the title.
     */
    public final String getTitle()
    {
        return title;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public final String toString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append( "Section  {title: '" );
        sb.append( getTitle() );
        sb.append( "' level: " );
        sb.append( getLevel() );
        sb.append( "}: [" );
        for ( final Block block : getBlocks() )
        {
            sb.append( block.toString() );
            sb.append( ", " );
        }
        sb.append( "]" );
        return sb.toString();
    }

}
