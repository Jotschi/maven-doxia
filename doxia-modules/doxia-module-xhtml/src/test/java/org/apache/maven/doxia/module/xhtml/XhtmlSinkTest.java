package org.apache.maven.doxia.module.xhtml;

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

import java.io.StringWriter;
import java.io.Writer;

import org.apache.maven.doxia.sink.AbstractSinkTest;
import org.apache.maven.doxia.sink.Sink;

/**
 * @author Jason van Zyl
 * @version $Id$
 * @since 1.0
 */
public class XhtmlSinkTest
    extends AbstractSinkTest
{
    /** {@inheritDoc} */
    protected String outputExtension()
    {
        return "xhtml";
    }

    /** {@inheritDoc} */
    protected Sink createSink( Writer writer )
    {
        return new XhtmlSink( writer, "UTF-8" );
    }

    /** {@inheritDoc} */
    protected boolean isXmlSink()
    {
        return true;
    }

    /**
     * Test link generation.
     *
     * @throws java.lang.Exception if any.
     */
    public void testLinks()
        throws Exception
    {
        XhtmlSink sink = null;
        Writer writer =  new StringWriter();
        try
        {
            sink = (XhtmlSink) createSink( writer );
            sink.link( "http:/www.xdoc.com" );
            sink.link_();
            sink.link( "./index.html#anchor" );
            sink.link_();
            sink.link( "../index.html#anchor" );
            sink.link_();
            sink.link( "index.html" );
            sink.link_();
        }
        finally
        {
            if ( sink != null )
            {
                sink.close();
            }
        }

        String actual = writer.toString();
        assertTrue( actual.indexOf( "<a class=\"externalLink\" href=\"http:/www.xdoc.com\"></a>" ) != -1 );
        assertTrue( actual.indexOf( "<a href=\"./index.html#anchor\"></a>" ) != -1 );
        assertTrue( actual.indexOf( "<a href=\"../index.html#anchor\"></a>" ) != -1 );
        assertTrue( actual.indexOf( "<a href=\"index.html\"></a>" ) != -1 );
    }

    /** {@inheritDoc} */
    protected String getTitleBlock( String title )
    {
        return "<title>" + title + "</title>";
    }

    /** {@inheritDoc} */
    protected String getAuthorBlock( String author )
    {
        return author;
    }

    /** {@inheritDoc} */
    protected String getDateBlock( String date )
    {
        return date;
    }

    /** {@inheritDoc} */
    protected String getHeadBlock()
    {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title></title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/></head>";
    }

    /** {@inheritDoc} */
    protected String getBodyBlock()
    {
        return "<body></body></html>";
    }

    /** {@inheritDoc} */
    protected String getSectionTitleBlock( String title )
    {
        return title;
    }

    /** {@inheritDoc} */
    protected String getSection1Block( String title )
    {
        return "<div class=\"section\"><h2>" + title + "</h2></div>";
    }

    /** {@inheritDoc} */
    protected String getSection2Block( String title )
    {
        return "<div class=\"section\"><h3>" + title + "</h3></div>";
    }

    /** {@inheritDoc} */
    protected String getSection3Block( String title )
    {
        return "<div class=\"section\"><h4>" + title + "</h4></div>";
    }

    /** {@inheritDoc} */
    protected String getSection4Block( String title )
    {
        return "<div class=\"section\"><h5>" + title + "</h5></div>";
    }

    /** {@inheritDoc} */
    protected String getSection5Block( String title )
    {
        return "<div class=\"section\"><h6>" + title + "</h6></div>";
    }

    /** {@inheritDoc} */
    protected String getListBlock( String item )
    {
        return "<ul><li>" + item + "</li></ul>";
    }

    /** {@inheritDoc} */
    protected String getNumberedListBlock( String item )
    {
        return "<ol style=\"list-style-type: lower-roman\"><li>" + item + "</li></ol>";
    }

    /** {@inheritDoc} */
    protected String getDefinitionListBlock( String definum, String definition )
    {
        return "<dl><dt>" + definum + "</dt><dd>" + definition + "</dd></dl>";
    }

    /** {@inheritDoc} */
    protected String getFigureBlock( String source, String caption )
    {
        return "<img src=\"" + source + "\" alt=\"" + caption + "\" />";
    }

    /** {@inheritDoc} */
    protected String getTableBlock( String cell, String caption )
    {
        return "<table border=\"0\" class=\"bodyTable\" align=\"center\">"
            + "<caption>Table caption</caption><tr class=\"a\"><td align=\"center\">cell</td></tr>"
            + "</table>";
    }

    // Disable testTable until the order of attributes issue is clarified
    // TODO: remove
    /** {@inheritDoc} */
    public void testTable()
    {
        assertEquals( "Dummy!", "", "" );
    }

    /** {@inheritDoc} */
    protected String getParagraphBlock( String text )
    {
        return "<p>" + text + "</p>";
    }

    /** {@inheritDoc} */
    protected String getVerbatimBlock( String text )
    {
        return "<div class=\"source\"><pre>" + text + "</pre></div>";
    }

    /** {@inheritDoc} */
    protected String getHorizontalRuleBlock()
    {
        return "<hr />";
    }

    /** {@inheritDoc} */
    protected String getPageBreakBlock()
    {
        return "<!-- PB -->";
    }

    /** {@inheritDoc} */
    protected String getAnchorBlock( String anchor )
    {
        return "<a name=\"" + anchor + "\">" + anchor + "</a>";
    }

    /** {@inheritDoc} */
    protected String getLinkBlock( String link, String text )
    {
        return "<a href=\"" + link + "\">" + text + "</a>";
    }

    /** {@inheritDoc} */
    protected String getItalicBlock( String text )
    {
        return "<i>" + text + "</i>";
    }

    /** {@inheritDoc} */
    protected String getBoldBlock( String text )
    {
        return "<b>" + text + "</b>";
    }

    /** {@inheritDoc} */
    protected String getMonospacedBlock( String text )
    {
        return "<tt>" + text + "</tt>";
    }

    /** {@inheritDoc} */
    protected String getLineBreakBlock()
    {
        return "<br />";
    }

    /** {@inheritDoc} */
    protected String getNonBreakingSpaceBlock()
    {
        return "&#160;";
    }

    /** {@inheritDoc} */
    protected String getTextBlock( String text )
    {
        // TODO: need to be able to retreive those from outside the sink
        return "~,_=,_-,_+,_*,_[,_],_&lt;,_&gt;,_{,_},_\\";
    }

    /** {@inheritDoc} */
    protected String getRawTextBlock( String text )
    {
        return text;
    }

    /**
     * Test entities is section titles and paragraphs.
     */
    public void testEntities()
    {
        XhtmlSink sink = null;
        Writer writer =  new StringWriter();

        try
        {
            sink = new XhtmlSink( writer );
            sink.section( Sink.SECTION_LEVEL_1, null );
            sink.sectionTitle( Sink.SECTION_LEVEL_1, null );
            sink.text( "&", null );
            sink.sectionTitle_( Sink.SECTION_LEVEL_1 );
            sink.paragraph( null );
            sink.text( "&", null );
            sink.paragraph_();
            sink.section_( Sink.SECTION_LEVEL_1 );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\"><h2>&amp;</h2><p>&amp;</p></div>", writer.toString() );
    }

    /**
     * Test head events.
     */
    public void testHead()
    {
        XhtmlSink sink = null;
        Writer writer =  new StringWriter();

        try
        {
            sink = new XhtmlSink( writer );
            sink.head();
            sink.title();
            sink.text( "Title" );
            sink.title_();
            sink.comment( "A comment" );
            sink.author();
            // note: this is really illegal, there should be no un-resolved entities emitted into text()
            sink.text( "&#x123;&" );
            sink.author_();
            sink.head_();
        }
        finally
        {
            sink.close();
        }

        String exp =
                "<head><title>Title</title><!-- A comment --><meta name=\"author\" content=\"&#x123;&amp;\" /></head>";
        assertTrue( writer.toString().indexOf( exp ) != -1 );
    }

    /** {@inheritDoc} */
    protected String getCommentBlock( String text )
    {
        return "<!-- Simple comment with - - - - -->";
    }
}
