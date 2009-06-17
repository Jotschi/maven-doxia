package org.apache.maven.doxia.module.fo;

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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.sink.AbstractXmlSink;
import org.apache.maven.doxia.sink.SinkEventAttributeSet;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.SinkUtils;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.apache.maven.doxia.util.HtmlTools;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;

/**
 * A Doxia Sink that produces a FO model. The usage is similar to the following:
 *
 * <pre>
 * FoSink sink = new FoSink( writer );
 * sink.beginDocument();
 * ...
 * sink.endDocument();
 * </pre>
 *
 * @author ltheussl
 * @version $Id$
 * @since 1.1
 */
public class FoSink
    extends AbstractXmlSink
    implements FoMarkup
{
    /** For writing the result. */
    private final Writer out;

    /** The StringWriter to write the result temporary, so we could play with the output and fix fo.
     * Calling the method {@link #close()} is needed to perform the changes in the {@link #out}. */
    private StringWriter tempWriter;

    /** Used to get the current position in numbered lists. */
    private final Stack listStack = new Stack();

    /** Used to get attributes for a given FO element. */
    private final FoConfiguration config;

    /** Counts the current section level. */
    private int section = 0;

    /** Counts the current subsection level. */
    private int subsection = 0;

    /** Counts the current subsubsection level. */
    private int subsubsection = 0;

    /** Drawing borders on table cells. */
    private boolean tableGrid;

    /** Alignment of table cells. */
    private int[] cellJustif;

    /** Justification of table cells. */
    private boolean isCellJustif;

    /** Current table cell. */
    private int cellCount;

    /** Verbatim flag. */
    private boolean verbatim;

    /** figure flag. */
    private boolean inFigure;

    private String encoding;

    private String languageId;

    private StringWriter tableCaptionWriter = null;

    private XMLWriter tableCaptionXMLWriter = null;

    /** Map of warn messages with a String as key to describe the error type and a Set as value.
     * Using to reduce warn messages. */
    protected Map warnMessages;

    /**
     * Constructor, initialize the Writer.
     *
     * @param writer not null writer to write the result. <b>Should</b> be an UTF-8 Writer.
     * You could use <code>newXmlWriter</code> methods from {@link org.codehaus.plexus.util.WriterFactory}.
     */
    protected FoSink( Writer writer )
    {
        this( writer, "UTF-8" );
    }

    /**
     * Constructor, initialize the Writer and tells which encoding is used.
     *
     * @param writer not null writer to write the result.
     * @param encoding the encoding used, that should be written to the generated HTML content
     * if not <code>null</code>.
     */
    protected FoSink( Writer writer, String encoding )
    {
        if ( writer == null )
        {
            throw new NullPointerException( "Null writer in FO Sink!" );
        }

        this.out = writer;
        this.tempWriter = new StringWriter();
        this.encoding = encoding;
        this.config = new FoConfiguration();

        setNameSpace( "fo" );
    }

    /**
     * Constructor, initialize the Writer and tells which encoding and languageId are used.
     *
     * @param writer not null writer to write the result.
     * @param encoding the encoding used, that should be written to the generated HTML content
     * if not <code>null</code>.
     * @param languageId language identifier for the root element as defined by
     * <a href="ftp://ftp.isi.edu/in-notes/bcp/bcp47.txt">IETF BCP 47</a>, Tags for the Identification of Languages;
     * in addition, the empty string may be specified.
     */
    protected FoSink( Writer writer, String encoding, String languageId )
    {
        this( writer, encoding );

        this.languageId = languageId;
    }

    // TODO add FOP compliance mode?

    /**
     * Load configuration parameters from a File.
     *
     * @param configFile the configuration file.
     *
     * @throws java.io.IOException if the File cannot be read
     *  or some error occurs when initializing the configuration parameters.
     *
     * @since 1.1.1
     */
    public void load( File configFile )
            throws IOException
    {
        config.load( configFile );
    }

    /** {@inheritDoc} */
    public void head( SinkEventAttributes attributes )
    {
        startPageSequence( "0", null, null );
    }

    /** {@inheritDoc} */
    public void head()
    {
        head( null );
    }

    /** {@inheritDoc} */
    public void head_()
    {
        writeEOL();
    }

    /** {@inheritDoc} */
    public void title( SinkEventAttributes attributes )
    {
        writeStartTag( BLOCK_TAG, "doc.header.title" );
    }

    /** {@inheritDoc} */
    public void title()
    {
        title( null );
    }

    /** {@inheritDoc} */
    public void title_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void author( SinkEventAttributes attributes )
    {
        writeStartTag( BLOCK_TAG, "doc.header.author" );
    }

    /** {@inheritDoc} */
    public void author()
    {
        author( null );
    }

    /** {@inheritDoc} */
    public void author_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void date( SinkEventAttributes attributes )
    {
        writeStartTag( BLOCK_TAG, "doc.header.date" );
    }

    /** {@inheritDoc} */
    public void date()
    {
        date( null );
    }

    /** {@inheritDoc} */
    public void date_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void body( SinkEventAttributes attributes )
    {
        // noop
    }

    /** {@inheritDoc} */
    public void body()
    {
        body( null );
    }

    /** {@inheritDoc} */
    public void body_()
    {
        writeEOL();
        writeEndTag( FLOW_TAG );
        writeEOL();
        writeEndTag( PAGE_SEQUENCE_TAG );
        writeEOL();
        endDocument();
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void sectionTitle()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void sectionTitle_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void section( int level, SinkEventAttributes attributes )
    {
        if ( level == SECTION_LEVEL_1 )
        {
            section++;
            subsection = 0;
            subsubsection = 0;
        }
        else if ( level == SECTION_LEVEL_2 )
        {
            subsection++;
            subsubsection = 0;
        }
        else if ( level == SECTION_LEVEL_3 )
        {
            subsubsection++;
        }

        onSection();
    }

    /** {@inheritDoc} */
    public void section_( int level )
    {
        onSection_();
    }

    /** {@inheritDoc} */
    public void sectionTitle( int level, SinkEventAttributes attributes )
    {
        onSectionTitle( level );
    }

    /** {@inheritDoc} */
    public void sectionTitle_( int level )
    {
        onSectionTitle_();
    }

    /** {@inheritDoc} */
    public void section1()
    {
        section( SECTION_LEVEL_1, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle1()
    {
        sectionTitle( SECTION_LEVEL_1, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle1_()
    {
        sectionTitle_( SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        section_( SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    public void section2()
    {
        section( SECTION_LEVEL_2, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle2()
    {
        sectionTitle( SECTION_LEVEL_2, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle2_()
    {
        sectionTitle_( SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    public void section2_()
    {
        section_( SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    public void section3()
    {
        section( SECTION_LEVEL_3, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle3()
    {
        sectionTitle( SECTION_LEVEL_3, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle3_()
    {
        sectionTitle_( SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    public void section3_()
    {
        section_( SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    public void section4()
    {
        section( SECTION_LEVEL_4, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle4()
    {
        sectionTitle( SECTION_LEVEL_4, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle4_()
    {
        sectionTitle_( SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    public void section4_()
    {
        section_( SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    public void section5()
    {
        section( SECTION_LEVEL_5, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle5()
    {
        sectionTitle( SECTION_LEVEL_5, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle5_()
    {
        sectionTitle_( SECTION_LEVEL_5 );
    }

    /** {@inheritDoc} */
    public void section5_()
    {
        section_( SECTION_LEVEL_5 );
    }

    /** Starts a section/subsection. */
    private void onSection()
    {
        writeEOL();
        writeStartTag( BLOCK_TAG, "body.text" );
    }

    /**
     * Starts a section title.
     *
     * @param depth The section level.
     */
    private void onSectionTitle( int depth )
    {
        StringBuffer title = new StringBuffer( 16 );

        title.append( getChapterString() );

        writeEOL();
        if ( depth == SECTION_LEVEL_1 )
        {
            writeStartTag( BLOCK_TAG, "body.h1" );
            title.append( section ).append( "   " );
        }
        else if ( depth == SECTION_LEVEL_2 )
        {
            writeStartTag( BLOCK_TAG, "body.h2" );
            title.append( section ).append( "." );
            title.append( subsection ).append( "   " );
        }
        else if ( depth == SECTION_LEVEL_3 )
        {
            writeStartTag( BLOCK_TAG, "body.h3" );
            title.append( section ).append( "." );
            title.append( subsection ).append( "." );
            title.append( subsubsection ).append( "   " );
        }
        else if ( depth == SECTION_LEVEL_4 )
        {
            writeStartTag( BLOCK_TAG, "body.h4" );
        }
        else
        {
            writeStartTag( BLOCK_TAG, "body.h5" );
        }

        write( title.toString() );
    }

    /** Ends a section title. */
    private void onSectionTitle_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** Ends a section/subsection. */
    private void onSection_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /**
     * Resets the section counter to 0.
     * Only useful for overriding classes, like AggregateSink, the FoSink puts everything into one chapter.
     */
    protected void resetSectionCounter()
    {
        this.section = 0;
    }

    /**
     * Returns the current chapter number as a string.
     * By default does nothing, gets overridden by AggregateSink.
     *
     * @return an empty String.
     */
    protected String getChapterString()
    {
        return "";
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void list( SinkEventAttributes attributes )
    {
        writeEOL();
        writeStartTag( LIST_BLOCK_TAG, "list" );
    }

    /** {@inheritDoc} */
    public void list()
    {
        list( null );
    }

    /** {@inheritDoc} */
    public void list_()
    {
        writeEndTag( LIST_BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void listItem( SinkEventAttributes attributes )
    {
        writeStartTag( LIST_ITEM_TAG, "list.item" );
        writeStartTag( LIST_ITEM_LABEL_TAG );
        writeStartTag( BLOCK_TAG );
        write( "&#8226;" ); // TODO customize?
        writeEndTag( BLOCK_TAG );
        writeEndTag( LIST_ITEM_LABEL_TAG );
        writeEOL();
        writeStartTag( LIST_ITEM_BODY_TAG, "list.item" );
        writeEOL();
        writeStartTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void listItem()
    {
        listItem( null );
    }

    /** {@inheritDoc} */
    public void listItem_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
        writeEndTag( LIST_ITEM_BODY_TAG );
        writeEOL();
        writeEndTag( LIST_ITEM_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering, SinkEventAttributes attributes )
    {
        listStack.push( new NumberedListItem( numbering ) );
        writeEOL();
        writeStartTag( LIST_BLOCK_TAG, "list" );
    }

    /** {@inheritDoc} */
    public void numberedList( int numbering )
    {
        numberedList( numbering, null );
    }

    /** {@inheritDoc} */
    public void numberedList_()
    {
        listStack.pop();
        writeEndTag( LIST_BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void numberedListItem( SinkEventAttributes attributes )
    {
        NumberedListItem current = (NumberedListItem) listStack.peek();
        current.next();

        writeStartTag( LIST_ITEM_TAG, "list.item" );

        writeEOL();
        writeStartTag( LIST_ITEM_LABEL_TAG );
        writeEOL();
        writeStartTag( BLOCK_TAG );
        write( current.getListItemSymbol() );
        writeEndTag( BLOCK_TAG );
        writeEOL();
        writeEndTag( LIST_ITEM_LABEL_TAG );
        writeEOL();

        writeStartTag( LIST_ITEM_BODY_TAG, "list.item" );
        writeEOL();
        writeStartTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void numberedListItem()
    {
        numberedListItem( null );
    }

    /** {@inheritDoc} */
    public void numberedListItem_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
        writeEndTag( LIST_ITEM_BODY_TAG );
        writeEOL();
        writeEndTag( LIST_ITEM_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void definitionList( SinkEventAttributes attributes )
    {
        writeEOL();
        writeStartTag( BLOCK_TAG, "dl" );
    }

    /** {@inheritDoc} */
    public void definitionList()
    {
        definitionList( null );
    }

    /** {@inheritDoc} */
    public void definitionList_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void definitionListItem( SinkEventAttributes attributes )
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definitionListItem()
    {
        definitionListItem( null );
    }

    /** {@inheritDoc} */
    public void definitionListItem_()
    {
        // nop
    }

    /** {@inheritDoc} */
    public void definedTerm( SinkEventAttributes attributes )
    {
        writeStartTag( BLOCK_TAG, "dt" );
    }

    /** {@inheritDoc} */
    public void definedTerm()
    {
        definedTerm( null );
    }

    /** {@inheritDoc} */
    public void definedTerm_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void definition( SinkEventAttributes attributes )
    {
        writeEOL();
        writeStartTag( BLOCK_TAG, "dd" );
    }

    /** {@inheritDoc} */
    public void definition()
    {
        definition( null );
    }

    /** {@inheritDoc} */
    public void definition_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void figure( SinkEventAttributes attributes )
    {
        this.inFigure = true;
        writeEOL();
        writeStartTag( BLOCK_TAG, "figure.display" );
    }

    /** {@inheritDoc} */
    public void figure()
    {
        figure( null );
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        this.inFigure = false;
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void figureGraphics( String name )
    {
        figureGraphics( name, null );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = config.getAttributeSet( "figure.graphics" );
        atts.addAttribute( Attribute.SRC.toString(), src );

        // http://xmlgraphics.apache.org/fop/graphics.html#resolution

        final String[] valids = new String[] {"content-height", "content-width", "height", "width"};
        final MutableAttributeSet filtered = SinkUtils.filterAttributes( attributes, valids );

        if ( filtered != null )
        {
            atts.addAttributes( filtered );
        }

        writeln( "<fo:external-graphic" + SinkUtils.getAttributeString( atts ) + "/>" );
    }

    /**
     * Flags if we are inside a figure.
     *
     * @return True if we are between {@link #figure()} and {@link #figure_()} calls.
     */
    protected boolean isFigure()
    {
        return this.inFigure;
    }

    /** {@inheritDoc} */
    public void figureCaption( SinkEventAttributes attributes )
    {
        writeStartTag( BLOCK_TAG, "figure.caption" );
    }

    /** {@inheritDoc} */
    public void figureCaption()
    {
        figureCaption( null );
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void paragraph()
    {
        paragraph( null );
    }

    /** {@inheritDoc} */
    public void paragraph( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = config.getAttributeSet( "normal.paragraph" );

        if ( attributes != null && attributes.isDefined( SinkEventAttributes.ALIGN ) )
        {
            atts.addAttribute( "text-align", attributes.getAttribute( SinkEventAttributes.ALIGN ) );
        }

        writeEOL();
        writeStartTag( BLOCK_TAG, atts );
    }

    /** {@inheritDoc} */
    public void paragraph_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void verbatim( SinkEventAttributes attributes )
    {
        this.verbatim = true;

        boolean boxed = false;

        if ( attributes != null && attributes.isDefined( SinkEventAttributes.DECORATION ) )
        {
            boxed =
                "boxed".equals( attributes.getAttribute( SinkEventAttributes.DECORATION ).toString() );
        }

        if ( boxed )
        {
            writeStartTag( BLOCK_TAG, "body.source" );
        }
        else
        {
            writeStartTag( BLOCK_TAG, "body.pre" );
        }
    }

    /** {@inheritDoc} */
    public void verbatim( boolean boxed )
    {
        verbatim( boxed ? SinkEventAttributeSet.BOXED : null );
    }

    /** {@inheritDoc} */
    public void verbatim_()
    {
        this.verbatim = false;
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void horizontalRule( SinkEventAttributes attributes )
    {
        writeEOL();
        writeEOL();
        writeStartTag( BLOCK_TAG );
        writeEmptyTag( LEADER_TAG, "body.rule" );
        writeEndTag( BLOCK_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void horizontalRule()
    {
        horizontalRule( null );
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        writeEmptyTag( BLOCK_TAG, "break-before", "page" );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void table( SinkEventAttributes attributes )
    {
        writeEOL();
        writeStartTag( BLOCK_TAG, "table.padding" );

        // <fo:table-and-caption> is XSL-FO 1.0 standard but still not implemented in FOP 0.95
        //writeStartTag( TABLE_AND_CAPTION_TAG );

        writeStartTag( TABLE_TAG, "table.layout" );
    }

    /** {@inheritDoc} */
    public void table()
    {
        table( null );
    }

    /** {@inheritDoc} */
    public void table_()
    {
        String tableCaption = null;
        if ( tableCaptionXMLWriter != null )
        {
            tableCaption = tableCaptionWriter.toString();
            tableCaptionXMLWriter = null;
            tableCaptionWriter = null;
        }

        String content = tempWriter.toString();
        if ( content.lastIndexOf( "<fo:table " ) != -1 || content.lastIndexOf( "<fo:table>" ) != -1 )
        {
            StringBuffer sb = new StringBuffer();
            // FOP hack to center the table, see
            // http://xmlgraphics.apache.org/fop/fo.html#fo-center-table-horizon
            sb.append( "<fo:table-column column-width=\"proportional-column-width(1)\"/>" );
            sb.append( EOL );

            int percent = 100 / cellCount;
            for ( int i = 0; i < cellCount; i++ )
            {
                sb.append( "<fo:table-column column-width=\"" + percent + "%\"/>" );
                sb.append( EOL );
            }

            sb.append( "<fo:table-column column-width=\"proportional-column-width(1)\"/>" );
            sb.append( EOL );

            String subContent;
            if ( content.lastIndexOf( "<fo:table " ) != -1 )
            {
                subContent = content.substring( content.lastIndexOf( "<fo:table " ) );
            }
            else
            {
                subContent = content.substring( content.lastIndexOf( "<fo:table>" ) );
            }
            String table = subContent.substring( 0, subContent.indexOf( ">" ) + 1 );
            String subContentUpdated = StringUtils.replace( subContent, table, table + EOL + sb.toString() );

            tempWriter = new StringWriter();
            tempWriter.write( StringUtils.replace( content, subContent, subContentUpdated ) );
        }

        writeEndTag( TABLE_TAG );
        writeEOL();

        // <fo:table-and-caption> is XSL-FO 1.0 standard but still not implemented in FOP 0.95
        //writeEndTag( TABLE_AND_CAPTION_TAG );

        writeEndTag( BLOCK_TAG );
        writeEOL();

        if ( tableCaption != null )
        {
            SinkEventAttributeSet atts = new SinkEventAttributeSet();
            atts.addAttribute( SinkEventAttributes.ALIGN, "center" );

            paragraph( atts );
            write( tableCaption );
            paragraph_();
        }
    }

    /** {@inheritDoc} */
    public void tableRows( int[] justification, boolean grid )
    {
        this.tableGrid = grid;
        this.cellJustif = justification;
        this.isCellJustif = true;
        writeEOL();
        writeStartTag( TABLE_BODY_TAG );
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        this.cellJustif = null;
        this.isCellJustif = false;
        writeEndTag( TABLE_BODY_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void tableRow( SinkEventAttributes attributes )
    {
        // TODO spacer rows
        writeStartTag( TABLE_ROW_TAG, "table.body.row" );
        this.cellCount = 0;
    }

    /** {@inheritDoc} */
    public void tableRow()
    {
        tableRow( null );
    }

    /** {@inheritDoc} */
    public void tableRow_()
    {
        writeEndTag( TABLE_ROW_TAG );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void tableCell( SinkEventAttributes attributes )
    {
        tableCell( false );
    }

    /** {@inheritDoc} */
    public void tableCell()
    {
        tableCell( (SinkEventAttributes) null );
    }

    /** {@inheritDoc} */
    public void tableCell( String width )
    {
        // TODO: fop can't handle cell width
        tableCell( false );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( SinkEventAttributes attributes )
    {
        tableCell( true );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
        tableHeaderCell( (SinkEventAttributes) null );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( String width )
    {
        // TODO: fop can't handle cell width
        tableCell( true );
    }

    /**
     * Writes a table cell.
     *
     * @param headerRow true if this is a header cell.
     */
    private void tableCell( boolean headerRow )
    {
         String justif = null;

         if ( cellJustif != null && isCellJustif )
         {
             switch ( cellJustif[Math.min( cellCount, cellJustif.length - 1 )] )
             {
                 case JUSTIFY_LEFT:
                     justif = "left";
                     break;
                 case JUSTIFY_RIGHT:
                     justif = "right";
                     break;
                 case JUSTIFY_CENTER:
                 default:
                     justif = "center";
                     break;
             }
         }

        MutableAttributeSet cellAtts = headerRow
                 ? config.getAttributeSet( "table.heading.cell" )
                 : config.getAttributeSet( "table.body.cell" );

        // the column-number is needed for the hack to center the table, see tableRows.
        cellAtts.addAttribute( "column-number", String.valueOf( cellCount + 2 ) );

        if ( tableGrid )
        {
            cellAtts.addAttributes( config.getAttributeSet( "table.body.cell.grid" ) );
        }

        MutableAttributeSet blockAtts = headerRow
                 ? config.getAttributeSet( "table.heading.block" )
                 : config.getAttributeSet( "table.body.block" );

        if ( justif != null )
        {
           blockAtts.addAttribute( "text-align", justif );
        }

        writeStartTag( TABLE_CELL_TAG, cellAtts );
        writeEOL();
        writeStartTag( BLOCK_TAG, blockAtts );
        writeEOL();
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        writeEndTag( BLOCK_TAG );
        writeEOL();
        writeEndTag( TABLE_CELL_TAG );
        writeEOL();

        if ( isCellJustif )
        {
            ++cellCount;
        }
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        tableCell_();
    }

    /** {@inheritDoc} */
    public void tableCaption( SinkEventAttributes attributes )
    {
        tableCaptionWriter = new StringWriter();
        tableCaptionXMLWriter = new PrettyPrintXMLWriter( tableCaptionWriter );

        // <fo:table-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.95
        //writeStartTag( TABLE_CAPTION_TAG );

        // TODO: how to implement this otherwise?
        // table-footer doesn't work because it has to be declared before table-body.
    }

    /** {@inheritDoc} */
    public void tableCaption()
    {
        tableCaption( null );
    }

    /** {@inheritDoc} */
    public void tableCaption_()
    {
        // <fo:table-caption> is XSL-FO 1.0 standard but not implemented in FOP 0.95
        //writeEndTag( TABLE_CAPTION_TAG );
    }

    /** {@inheritDoc} */
    public void anchor( String name, SinkEventAttributes attributes )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Anchor name cannot be null!" );
        }

        String anchor = name;

        if ( !DoxiaUtils.isValidId( anchor ) )
        {
            anchor = DoxiaUtils.encodeId( name, true );

            String msg = "Modified invalid anchor name: '" + name + "' to '" + anchor + "'";
            logMessage( "modifiedLink", msg );
        }

        anchor = "#" + name;

        writeStartTag( INLINE_TAG, "id", anchor );
    }

    /** {@inheritDoc} */
    public void anchor( String name )
    {
        anchor( name, null );
    }

    /** {@inheritDoc} */
    public void anchor_()
    {
        writeEndTag( INLINE_TAG );
    }

    /** {@inheritDoc} */
    public void link( String name, SinkEventAttributes attributes )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Link name cannot be null!" );
        }

        if ( DoxiaUtils.isExternalLink( name ) )
        {
            writeStartTag( BASIC_LINK_TAG, "external-destination", HtmlTools.escapeHTML( name ) );
            writeStartTag( INLINE_TAG, "href.external" );
        }
        else if ( DoxiaUtils.isInternalLink( name ) )
        {
            String anchor = name.substring( 1 );

            if ( !DoxiaUtils.isValidId( anchor ) )
            {
                anchor = DoxiaUtils.encodeId( anchor, true );

                String msg = "Modified invalid anchor name: '" + name + "' to '" + anchor + "'";
                logMessage( "modifiedLink", msg );
            }

            anchor = "#" + anchor;

            writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( INLINE_TAG, "href.internal" );
        }
        else
        {
            // treat everything else as is
            String anchor = name;

            writeStartTag( BASIC_LINK_TAG, "internal-destination", HtmlTools.escapeHTML( anchor ) );
            writeStartTag( INLINE_TAG, "href.internal" );
        }
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        link( name, null );
    }

    /** {@inheritDoc} */
    public void link_()
    {
        writeEndTag( INLINE_TAG );
        writeEndTag( BASIC_LINK_TAG );
    }

    /** {@inheritDoc} */
    public void italic()
    {
        writeStartTag( INLINE_TAG, "italic" );
    }

    /** {@inheritDoc} */
    public void italic_()
    {
        writeEndTag( INLINE_TAG );
    }

    /** {@inheritDoc} */
    public void bold()
    {
        writeStartTag( INLINE_TAG, "bold" );
    }

    /** {@inheritDoc} */
    public void bold_()
    {
        writeEndTag( INLINE_TAG );
    }

    /** {@inheritDoc} */
    public void monospaced()
    {
        writeStartTag( INLINE_TAG, "monospace" );
    }

    /** {@inheritDoc} */
    public void monospaced_()
    {
        writeEndTag( INLINE_TAG );
    }

    /** {@inheritDoc} */
    public void lineBreak( SinkEventAttributes attributes )
    {
        writeEOL();
        writeEOL();
        writeSimpleTag( BLOCK_TAG );
    }

    /** {@inheritDoc} */
    public void lineBreak()
    {
        lineBreak( null );
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        write( "&#160;" );
    }

    /** {@inheritDoc} */
    public void text( String text, SinkEventAttributes attributes )
    {
        content( text );
    }

    /** {@inheritDoc} */
    public void text( String text )
    {
        text( text, null );
    }

    /** {@inheritDoc} */
    public void rawText( String text )
    {
        write( text );
    }

    /** {@inheritDoc} */
    public void flush()
    {
        try
        {
            out.flush();
        }
        catch ( IOException e )
        {
            getLog().debug( e );
        }
    }

    /** {@inheritDoc} */
    public void close()
    {
        try
        {
            out.write( tempWriter.toString() );
            tempWriter = new StringWriter();
            out.close();
        }
        catch ( IOException e )
        {
            getLog().debug( e );
        }

        if ( getLog().isWarnEnabled() && this.warnMessages != null )
        {
            for ( Iterator it = this.warnMessages.entrySet().iterator(); it.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) it.next();

                Set set = (Set) entry.getValue();

                for ( Iterator it2 = set.iterator(); it2.hasNext(); )
                {
                    String msg = (String) it2.next();

                    getLog().warn( msg );
                }
            }

            this.warnMessages = null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Unkown events just log a warning message but are ignored otherwise.
     * @see org.apache.maven.doxia.sink.Sink#unknown(String,Object[],SinkEventAttributes)
     */
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        String msg = "Unknown Sink event: '" + name + "', ignoring!";
        logMessage( "unknownEvent", msg );
    }

    /** {@inheritDoc} */
    public void comment( String comment )
    {

        if ( StringUtils.isNotEmpty( comment ) && comment.indexOf( "--" ) != -1 )
        {
            String originalComment = comment;
            // http://www.w3.org/TR/2000/REC-xml-20001006#sec-comments
            while ( comment.indexOf( "--" ) != -1 )
            {
                comment = StringUtils.replace( comment, "--", "- -" );
            }

            String msg = "Modified invalid comment: '" + originalComment + "' to '" + comment + "'";
            logMessage( "modifyComment", msg );
        }

        StringBuffer buf = new StringBuffer( comment.length() + 9 );

        buf.append( LESS_THAN ).append( BANG ).append( MINUS ).append( MINUS ).append( SPACE );
        buf.append( comment );
        buf.append( SPACE ).append( MINUS ).append( MINUS ).append( GREATER_THAN );

        write( buf.toString() );
    }

    /**
     * Writes the beginning of a FO document.
     */
    public void beginDocument()
    {
        write( "<?xml version=\"1.0\"" );
        if ( encoding != null )
        {
            write( " encoding=\"" + encoding + "\"" );
        }
        write( "?>" );
        writeEOL();

        MutableAttributeSet atts = new SinkEventAttributeSet();
        atts.addAttribute( "xmlns:" + getNameSpace(), FO_NAMESPACE );

        if ( languageId != null )
        {
            atts.addAttribute( "language", languageId );
        }

        writeStartTag( ROOT_TAG, atts );

        writeStartTag( LAYOUT_MASTER_SET_TAG );

        writeStartTag( SIMPLE_PAGE_MASTER_TAG, "layout.master.set.cover-page" );
        writeEmptyTag( REGION_BODY_TAG, "layout.master.set.cover-page.region-body" );
        writeEndTag( SIMPLE_PAGE_MASTER_TAG );
        writeEOL();

        writeStartTag( SIMPLE_PAGE_MASTER_TAG, "layout.master.set.toc" );
        writeEmptyTag( REGION_BODY_TAG, "layout.master.set.toc.region-body" );
        writeEmptyTag( REGION_BEFORE_TAG, "layout.master.set.toc.region-before" );
        writeEmptyTag( REGION_AFTER_TAG, "layout.master.set.toc.region-after" );
        writeEndTag( SIMPLE_PAGE_MASTER_TAG );
        writeEOL();

        writeStartTag( SIMPLE_PAGE_MASTER_TAG, "layout.master.set.body" );
        writeEmptyTag( REGION_BODY_TAG, "layout.master.set.body.region-body" );
        writeEmptyTag( REGION_BEFORE_TAG, "layout.master.set.body.region-before" );
        writeEmptyTag( REGION_AFTER_TAG, "layout.master.set.body.region-after" );
        writeEndTag( SIMPLE_PAGE_MASTER_TAG );
        writeEOL();

        writeEndTag( LAYOUT_MASTER_SET_TAG );
        writeEOL();

        pdfBookmarks();
    }

    /**
     * Writes the end of a FO document, flushes and closes the stream.
     */
    public void endDocument()
    {
        writeEndTag( ROOT_TAG );
        writeEOL();

        flush();
        close();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Returns the configuration object of this sink.
     *
     * @return The configuration object of this sink.
     */
    protected FoConfiguration getFoConfiguration()
    {
        return config;
    }

    /**
     * Writes a start tag, prepending EOL.
     *
     * @param tag The tag.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeStartTag( Tag tag, String attributeId )
    {
        writeEOL();
        writeStartTag( tag, config.getAttributeSet( attributeId ) );
    }

    /**
     * Writes a start tag, prepending EOL.
     *
     * @param tag The tag.
     * @param id An id to add.
     * @param name The name (value) of the id.
     */
    protected void writeStartTag( Tag tag, String id, String name )
    {
        writeEOL();
        MutableAttributeSet att = new SinkEventAttributeSet( new String[] {id, name} );

        writeStartTag( tag, att );
    }

    /**
     * Writes a start tag, prepending EOL.
     *
     * @param tag The tag.
     * @param id An id to add.
     * @param name The name (value) of the id.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeStartTag( Tag tag, String id, String name, String attributeId )
    {
        MutableAttributeSet att = config.getAttributeSet( attributeId );

        // make sure we don't add it twice
        if ( att.isDefined( id ) )
        {
            att.removeAttribute( id );
        }

        att.addAttribute( id, name );

        writeEOL();
        writeStartTag( tag, att );
    }

    /**
     * Writes an empty tag, prepending EOL.
     *
     * @param tag The tag.
     * @param id An id to add.
     * @param name The name (value) of the id.
     */
    protected void writeEmptyTag( Tag tag, String id, String name )
    {
        MutableAttributeSet att = new SinkEventAttributeSet( new String[] {id, name} );

        writeEOL();
        writeSimpleTag( tag, att );
    }

    /**
     * Writes a simple tag, appending EOL.
     *
     * @param tag The tag name.
     * @param attributeId An id identifying the attribute set.
     */
    protected void writeEmptyTag( Tag tag, String attributeId )
    {
        writeEOL();
        writeSimpleTag( tag, config.getAttributeSet( attributeId ) );
    }

    /**
     * {@inheritDoc}
     *
     * Writes a text, swallowing any exceptions.
     */
    protected void write( String text )
    {
        if ( tableCaptionXMLWriter == null )
        {
            tempWriter.write( unifyEOLs( text ) );
        }
        else
        {
            tableCaptionXMLWriter.writeText( unifyEOLs( text ) );
        }
    }

    /**
     * Writes a text, appending EOL.
     *
     * @param text The text to write.
     */
    protected void writeln( String text )
    {
        write( text );
        writeEOL();
    }

    /**
     * Writes content, escaping special characters.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        write( escaped( text, verbatim ) );
    }

    /**
     * Escapes special characters so that the text can be included in a fo file.
     *
     * @param text The text to process.
     * @param verb In verbatim mode, white space and newlines are escaped.
     * @return The text with special characters escaped.
     */
    public static String escaped( String text, boolean verb )
    {
        int length = text.length();
        StringBuffer buffer = new StringBuffer( length );

        for ( int i = 0; i < length; ++i )
        {
            char c = text.charAt( i );
            switch ( c )
            {
                case ' ':
                    if ( verb )
                    {
                        buffer.append( "&#160;" );
                    }
                    else
                    {
                        buffer.append( c );
                    }
                    break;
                case '<':
                    buffer.append( "&lt;" );
                    break;
                case '>':
                    buffer.append( "&gt;" );
                    break;
                case '&':
                    buffer.append( "&amp;" );
                    break;
                case '\n':
                    buffer.append( EOL );
                    if ( verb )
                    {
                        buffer.append( "<fo:block/>" + EOL );
                    }
                    break;
                default:
                    if ( needsSymbolFont( c ) )
                    {
                        // TODO: make font configurable?
                        buffer.append( "<fo:inline font-family=\"Symbol\">" ).append( c ).append( "</fo:inline>" );
                    }
                    else
                    {
                        buffer.append( c );
                    }
            }
        }

        return buffer.toString();
    }

    /** {@inheritDoc} */
    protected void writeStartTag( Tag t, MutableAttributeSet att, boolean isSimpleTag )
    {
        if ( tableCaptionXMLWriter == null )
        {
            super.writeStartTag ( t, att, isSimpleTag );
        }
        else
        {
            String tag = ( getNameSpace() != null ? getNameSpace() + ":" : "" ) + t.toString();
            tableCaptionXMLWriter.startElement( tag );

            if ( att != null )
            {
                Enumeration names = att.getAttributeNames();
                while ( names.hasMoreElements() )
                {
                    Object key = names.nextElement();
                    Object value = att.getAttribute( key );

                    tableCaptionXMLWriter.addAttribute( key.toString(), value.toString() );
                }
            }

            if ( isSimpleTag )
            {
                tableCaptionXMLWriter.endElement();
            }
        }
    }

    /** {@inheritDoc} */
    protected void writeEndTag( Tag t )
    {
        if ( tableCaptionXMLWriter == null )
        {
            super.writeEndTag( t );
        }
        else
        {
            tableCaptionXMLWriter.endElement();
        }
    }

    private static final char UPPER_ALPHA = 0x391;
    private static final char PIV = 0x3d6;
    private static final char OLINE = 0x203e;
    private static final char DIAMS = 0x2666;
    private static final char EURO = 0x20ac;
    private static final char TRADE = 0x2122;

    private static boolean needsSymbolFont( char c )
    {
        // greek characters and mathematical symbols, except the euro and trade symbols
        // symbols I couldn't get to display in any font:
        // zwnj (0x200C), zwj (0x200D), lrm (0x200E), rlm (0x200F), oline (0x203E), prime (0x2032),
        // Prime (0x2033), lceil (0x2038), rceil (0x2039), lfloor (0x203A), rfloor (0x203B)
        return ( c >= UPPER_ALPHA && c <= PIV )
                || ( c >= OLINE && c <= DIAMS && c != EURO && c != TRADE );
    }

    /**
     * Starts a page sequence.
     *
     * @param initPageNumber The initial page number. Should be either "0" (for the first page) or "auto".
     * @param headerText The text to write in the header, if null, nothing is written.
     * @param footerText The text to write in the footer, if null, nothing is written.
     */
    protected void startPageSequence( String initPageNumber, String headerText, String footerText )
    {
        writeln( "<fo:page-sequence initial-page-number=\"" + initPageNumber + "\" master-reference=\"body\">" );
        regionBefore( headerText );
        regionAfter( footerText );
        writeln( "<fo:flow flow-name=\"xsl-region-body\">" );
        chapterHeading( null, true );
    }

    /**
     * Writes a 'xsl-region-before' block.
     *
     * @param headerText The text to write in the header, if null, nothing is written.
     */
    protected void regionBefore( String headerText )
    {
        // do nothing, overridden by AggregateSink
    }

    /**
     * Writes a 'xsl-region-after' block. By default does nothing, gets overridden by AggregateSink.
     *
     * @param footerText The text to write in the footer, if null, nothing is written.
     */
    protected void regionAfter( String footerText )
    {
        // do nothing, overridden by AggregateSink
    }

    /**
     * Writes a chapter heading. By default does nothing, gets overridden by AggregateSink.
     *
     * @param headerText The text to write in the header, if null, the current document title is written.
     * @param chapterNumber True if the chapter number should be written in front of the text.
     */
    protected void chapterHeading( String headerText, boolean chapterNumber )
    {
        // do nothing, overridden by AggregateSink
    }

    /**
     * Writes a fo:bookmark-tree. By default does nothing, gets overridden by AggregateSink.
     */
    protected void pdfBookmarks()
    {
        // do nothing, overridden by AggregateSink
    }

    /**
     * If debug mode is enabled, log the <code>msg</code> as is, otherwise add unique msg in <code>warnMessages</code>.
     *
     * @param key not null
     * @param msg not null
     * @see #close()
     * @since 1.1.1
     */
    protected void logMessage( String key, String msg )
    {
        msg = "[FO Sink] " + msg;
        if ( getLog().isDebugEnabled() )
        {
            getLog().debug( msg );

            return;
        }

        if ( warnMessages == null )
        {
            warnMessages = new HashMap();
        }

        Set set = (Set) warnMessages.get( key );
        if ( set == null )
        {
            set = new TreeSet();
        }
        set.add( msg );
        warnMessages.put( key, set );
    }
}
