package org.apache.maven.doxia.sink;

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

import org.codehaus.plexus.PlexusTestCase;

/**
 * Test for XhtmlBaseSink.
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.1
 */
public class XhtmlBaseSinkTest
    extends PlexusTestCase
{
    private final SinkEventAttributes attributes =
            new SinkEventAttributeSet( new String[] {SinkEventAttributes.STYLE, "bold"} );
    private XhtmlBaseSink sink;
    private Writer writer;

    /**
     * Set up the writer.
     *
     * @throws java.lang.Exception if any.
     */
    protected void setUp()
            throws Exception
    {
        super.setUp();
        writer =  new StringWriter();
    }

    /** @throws Exception */
    public void testSpaceAfterClosingTag()
        throws Exception
    {
        // DOXIA-189
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.paragraph();
            sink.text( "There should be no space before the " );
            sink.italic();
            sink.text( "period" );
            sink.italic_();
            sink.text( "." );
            sink.paragraph_();
        }
        finally
        {
            if ( sink != null )
            {
                sink.close();
            }
        }

        String actual = writer.toString();
        String expected = "<p>There should be no space before the <i>period</i>.</p>";

        assertEquals( expected, actual );
    }

    /** @throws Exception */
    public void testNestedTables()
        throws Exception
    {
        // DOXIA-177

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.table();
            sink.tableRows( new int[] {0}, false );
            sink.tableCaption();
            sink.text( "caption1" );
            sink.tableCaption_();
            sink.tableRow();
            sink.tableCell();
            sink.table();
            sink.tableRows( new int[] {0}, false );
            sink.tableRow();
            sink.tableCell();
            sink.text( "nestedTableCell" );
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.tableCaption();
            sink.text( "caption2" );
            sink.tableCaption_();
            sink.table_();
            sink.tableCell_();
            sink.tableRow_();
            sink.tableRows_();
            sink.table_();
        }
        finally
        {
            sink.close();
        }

        String actual = writer.toString();
        assertTrue( actual.indexOf( "nestedTableCell" ) != 1 );
        assertTrue( actual.indexOf( "class=\"bodyTable\"><caption>caption1</caption><tr" ) != 1 );
        assertTrue( actual.indexOf( "class=\"bodyTable\"><caption>caption2</caption><tr" ) != 1 );
    }

    /**
     * Test of section method, of class XhtmlBaseSink.
     */
    public void testSection()
    {
        final int level = XhtmlBaseSink.SECTION_LEVEL_1;

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section( level, attributes );
            sink.sectionTitle( level, attributes );
            sink.sectionTitle_( level );
            sink.section_( level );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\" style=\"bold\"><h2 style=\"bold\"></h2></div>", writer.toString() );
    }

    /**
     * Test of section1 method, of class XhtmlBaseSink.
     */
    public void testSection1()
    {

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section1();
            sink.sectionTitle1();
            sink.sectionTitle1_();
            sink.section1_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\"><h2></h2></div>", writer.toString() );
    }

    /**
     * Test of section2 method, of class XhtmlBaseSink.
     */
    public void testSection2()
    {

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section2();
            sink.sectionTitle2();
            sink.sectionTitle2_();
            sink.section2_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\"><h3></h3></div>", writer.toString() );
    }

    /**
     * Test of section3 method, of class XhtmlBaseSink.
     */
    public void testSection3()
    {

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section3();
            sink.sectionTitle3();
            sink.sectionTitle3_();
            sink.section3_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\"><h4></h4></div>", writer.toString() );
    }

    /**
     * Test of section4 method, of class XhtmlBaseSink.
     */
    public void testSection4()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section4();
            sink.sectionTitle4();
            sink.sectionTitle4_();
            sink.section4_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\"><h5></h5></div>", writer.toString() );
    }

    /**
     * Test of section5 method, of class XhtmlBaseSink.
     */
    public void testSection5()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.section5();
            sink.sectionTitle5();
            sink.sectionTitle5_();
            sink.section5_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"section\"><h6></h6></div>", writer.toString() );
    }

    /**
     * Test of list method, of class XhtmlBaseSink.
     * @throws java.lang.Exception if any.
     */
    public void testList()
            throws Exception
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.list();
            sink.listItem();
            sink.listItem_();
            sink.list_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<ul><li></li></ul>", writer.toString() );

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.list( attributes );
            sink.listItem( attributes );
            sink.listItem_();
            sink.list_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<ul style=\"bold\"><li style=\"bold\"></li></ul>", writer.toString() );
    }

    /**
     * Test of numberedList method, of class XhtmlBaseSink.
     */
    public void testNumberedList()
    {
        final int numbering = XhtmlBaseSink.NUMBERING_DECIMAL;

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.numberedList( numbering );
            sink.numberedListItem();
            sink.numberedListItem_();
            sink.numberedList_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<ol style=\"list-style-type: decimal\"><li></li></ol>", writer.toString() );

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.numberedList( numbering, attributes );
            sink.numberedListItem( attributes );
            sink.numberedListItem_();
            sink.numberedList_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<ol style=\"list-style-type: decimal\"><li style=\"bold\"></li></ol>", writer.toString() );
    }

    /**
     * Test of definitionList method, of class XhtmlBaseSink.
     */
    public void testDefinitionList()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.definitionList();
            sink.definedTerm();
            sink.definedTerm_();
            sink.definition();
            sink.definition_();
            sink.definitionList_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<dl><dt></dt><dd></dd></dl>", writer.toString() );

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.definitionList( attributes );
            sink.definedTerm( attributes );
            sink.definedTerm_();
            sink.definition( attributes );
            sink.definition_();
            sink.definitionList_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<dl style=\"bold\"><dt style=\"bold\"></dt><dd style=\"bold\"></dd></dl>", writer.toString() );
    }

    /**
     * Test of figure method, of class XhtmlBaseSink.
     */
    public void testFigure()
    {
        final String src = "src.jpg";

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.figure( attributes );
            sink.figureGraphics( src, attributes );
            sink.figureCaption( attributes );
            sink.figureCaption_();
            sink.figure_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div style=\"bold\" class=\"figure\">"
                + "<p align=\"center\"><img src=\"src.jpg\" style=\"bold\" alt=\"\" /></p>"
                + "<p align=\"center\" style=\"bold\"><i></i></p></div>", writer.toString() );
    }

    /**
     * Test of figureGraphics method, of class XhtmlBaseSink.
     */
    public void testFigureGraphics()
    {
        String src = "source.png";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.figureGraphics( src, attributes );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<img src=\"source.png\" style=\"bold\" alt=\"\" />", writer.toString() );
    }

    /**
     * Test of paragraph method, of class XhtmlBaseSink.
     */
    public void testParagraph()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.paragraph();
            sink.paragraph_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<p></p>", writer.toString() );

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.paragraph( attributes );
            sink.paragraph_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<p style=\"bold\"></p>", writer.toString() );
    }

    /**
     * Test of verbatim method, of class XhtmlBaseSink.
     */
    public void testVerbatim()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.verbatim( true );
            sink.verbatim_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div class=\"source\"><pre></pre></div>", writer.toString() );

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.verbatim( attributes );
            sink.verbatim_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<div style=\"bold\"><pre style=\"bold\"></pre></div>", writer.toString() );
    }

    /**
     * Test of horizontalRule method, of class XhtmlBaseSink.
     */
    public void testHorizontalRule()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.horizontalRule();
            sink.horizontalRule( attributes );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<hr /><hr style=\"bold\" />", writer.toString() );
    }

    /**
     * Test of table method, of class XhtmlBaseSink.
     */
    public void testTable()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.table( attributes );
            sink.table_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "</table>", writer.toString() );
    }

    /**
     * Test of tableRows method, of class XhtmlBaseSink.
     */
    public void testTableRows()
    {
        final int[] justification = null;
        final boolean grid = false;

        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.tableRows( justification, grid );
            sink.tableRows_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<table align=\"center\" border=\"0\" class=\"bodyTable\">", writer.toString() );
    }

    /**
     * Test of tableRow method, of class XhtmlBaseSink.
     */
    public void testTableRow()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.tableRow( attributes );
            sink.tableRow_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<tr class=\"a\" style=\"bold\"></tr>", writer.toString() );
    }

    /**
     * Test of tableCell method, of class XhtmlBaseSink.
     */
    public void testTableCell()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.tableCell( attributes );
            sink.tableCell_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<td style=\"bold\"></td>", writer.toString() );
    }

    /**
     * Test of tableHeaderCell method, of class XhtmlBaseSink.
     */
    public void testTableHeaderCell()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.tableHeaderCell( attributes );
            sink.tableHeaderCell_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<th style=\"bold\"></th>", writer.toString() );
    }

    /**
     * Test of tableCaption method, of class XhtmlBaseSink.
     */
    public void testTableCaption()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );

            sink.tableCaption( attributes );
            sink.tableCaption_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<caption style=\"bold\"></caption>", writer.toString() );
    }

    /**
     * Test of anchor method, of class XhtmlBaseSink.
     */
    public void testAnchor()
    {
        String name = "anchor";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.anchor( name, attributes );
            sink.anchor_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<a name=\"anchor\" style=\"bold\"></a>", writer.toString() );
    }

    /**
     * Test of link method, of class XhtmlBaseSink.
     */
    public void testLink()
    {
        final String name = "link.html";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.link( name, attributes );
            sink.link_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<a href=\"link.html\" style=\"bold\"></a>", writer.toString() );
    }

    /**
     * Test of italic/bold/monospaced method, of class XhtmlBaseSink.
     */
    public void testItalic()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.italic();
            sink.italic_();
            sink.bold();
            sink.bold_();
            sink.monospaced();
            sink.monospaced_();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<i></i><b></b><tt></tt>", writer.toString() );
    }

    /**
     * Test of lineBreak/pageBreak/nonBreakingSpace method, of class XhtmlBaseSink.
     */
    public void testLineBreak()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.lineBreak( attributes );
            sink.pageBreak();
            sink.nonBreakingSpace();
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<br style=\"bold\" /><!-- PB -->&#160;", writer.toString() );
    }

    /**
     * Test of text method, of class XhtmlBaseSink.
     */
    public void testText()
    {
        String text = "a text & \u00c6";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.text( text );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "a text &amp; &#xc6;", writer.toString() );

        writer =  new StringWriter();

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.text( text, attributes );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "a text &amp; &#xc6;", writer.toString() );
    }

    /**
     * Test of rawText method, of class XhtmlBaseSink.
     */
    public void testRawText()
    {
        String text = "raw text";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.rawText( text );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "raw text", writer.toString() );
    }

    /**
     * Test of comment method, of class XhtmlBaseSink.
     */
    public void testComment()
    {
        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.comment( "a comment" );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "<!-- a comment -->", writer.toString() );
    }

    /**
     * Test of unknown method, of class XhtmlBaseSink.
     */
    public void testUnknown()
    {
        final String name = "unknown";
        final Object[] requiredParams = null;

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.unknown( name, requiredParams, attributes );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "", writer.toString() );
    }

    /**
     * Test of entity.
     */
    public void testEntity()
    {
        // DOXIA-314
        String text = "a text '&#x1d7ed;'";

        try
        {
            sink = new XhtmlBaseSink( writer );
            sink.text( text );
        }
        finally
        {
            sink.close();
        }

        assertEquals( "a text '&#x1d7ed;'", writer.toString() );
    }
}
