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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.util.DoxiaUtils;
import org.apache.maven.doxia.util.HtmlTools;
import org.codehaus.plexus.util.StringUtils;

/**
 * Abstract base xhtml sink implementation.
 *
 * @author Jason van Zyl
 * @author ltheussl
 * @version $Id$
 * @since 1.0
 */
public class XhtmlBaseSink
    extends AbstractXmlSink
    implements HtmlMarkup
{
    // ----------------------------------------------------------------------
    // Instance fields
    // ----------------------------------------------------------------------

    /** The PrintWriter to write the result. */
    private PrintWriter writer;

    /** Used to collect text events. */
    private StringBuffer buffer = new StringBuffer();

    /** An indication on if we're inside a head. */
    private boolean headFlag;

    /** An indication on if we're inside an image caption flag. */
    private boolean figureCaptionFlag;

    /** An indication on if we're inside a paragraph flag. */
    private boolean paragraphFlag;

    /** An indication on if we're in verbatim mode. */
    private boolean verbatimFlag;

    /** Alignment of table cells. */
    private int[] cellJustif;

    /** Justification of table cells. */
    private boolean isCellJustif;

    /** Number of cells in a table row. */
    private int cellCount;

    /** Used to style successive table rows differently. */
    private boolean evenTableRow = true;

    /** used to store attributes passed to table(). */
    private MutableAttributeSet tableAttributes;

    /** Used to distinguish old-style figure handling. */
    private boolean legacyFigure;

    /** Used to distinguish old-style figure handling. */
    private boolean legacyFigureCaption;

    /** Indicates that an image is part of a figure. */
    private boolean inFigure;

    /** The StringWriter to write the table content (DOXIA-177). */
    private StringWriter tableWriter;

    // ----------------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------------

    /**
     * Constructor, initialize the PrintWriter.
     *
     * @param out The writer to write the result.
     */
    public XhtmlBaseSink( Writer out )
    {
        this.writer = new PrintWriter( out );
    }

    // ----------------------------------------------------------------------
    // Accessor methods
    // ----------------------------------------------------------------------

    /**
     * @return the current buffer.
     */
    protected StringBuffer getBuffer()
    {
        return buffer;
    }

    /**
     * @param headFlag an header flag.
     */
    protected void setHeadFlag( boolean headFlag )
    {
        this.headFlag = headFlag;
    }

    /**
     * @return the current headFlag.
     */
    protected boolean isHeadFlag()
    {
        return this.headFlag ;
    }

    /**
     * @param verb a verbatim flag.
     */
    protected void setVerbatimFlag( boolean verb )
    {
        this.verbatimFlag = verb;
    }

    /**
     * @return the current verbatim flag.
     */
    protected boolean isVerbatimFlag()
    {
        return this.verbatimFlag ;
    }

    /**
     * @param justif the new cell justification array.
     */
    protected void setCellJustif( int[] justif )
    {
        this.cellJustif = justif;
        this.isCellJustif = true;
    }

    /**
     * @return the current cell justification array.
     */
    protected int[] getCellJustif()
    {
        return this.cellJustif ;
    }

    /**
     * @param count the new cell count.
     */
    protected void setCellCount( int count )
    {
        this.cellCount = count;
    }

    /**
     * @return the current cell count.
     */
    protected int getCellCount()
    {
        return this.cellCount ;
    }

    /**
     * @param even an evenTableRow flag.
     */
    protected void setEvenTableRow( boolean even )
    {
        this.evenTableRow = even;
    }

    /**
     * @return the current evenTableRow flag.
     */
    protected boolean isEvenTableRow()
    {
        return this.evenTableRow ;
    }

    /**
     * Reset all variables.
     */
    protected void resetState()
    {
        resetBuffer();
        headFlag = false;
        verbatimFlag = false;
        cellJustif = null;
        isCellJustif = false;
        cellCount = 0;
        evenTableRow = true;
    }

    /**
     * Reset the buffer.
     */
    protected void resetBuffer()
    {
        this.buffer = new StringBuffer();
    }


    // ----------------------------------------------------------------------
    // Sections
    // ----------------------------------------------------------------------

    /** {@inheritDoc} */
    public void section( int level, SinkEventAttributes attributes )
    {
        onSection( level, attributes );
    }

    /** {@inheritDoc} */
    public void sectionTitle( int level, SinkEventAttributes attributes )
    {
        onSectionTitle( level, attributes );
    }

    public void sectionTitle_( int level )
    {
        onSectionTitle_( level );
    }

    public void section_( int level )
    {
        onSection_( level );
    }

    /** {@inheritDoc} */
    public void section1()
    {
        onSection( SECTION_LEVEL_1, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle1()
    {
        onSectionTitle( SECTION_LEVEL_1, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle1_()
    {
        onSectionTitle_( SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    public void section1_()
    {
        onSection_( SECTION_LEVEL_1 );
    }

    /** {@inheritDoc} */
    public void section2()
    {
        onSection( SECTION_LEVEL_2, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle2()
    {
        onSectionTitle( SECTION_LEVEL_2, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle2_()
    {
        onSectionTitle_( SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    public void section2_()
    {
        onSection_( SECTION_LEVEL_2 );
    }

    /** {@inheritDoc} */
    public void section3()
    {
        onSection( SECTION_LEVEL_3, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle3()
    {
        onSectionTitle( SECTION_LEVEL_3, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle3_()
    {
        onSectionTitle_( SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    public void section3_()
    {
        onSection_( SECTION_LEVEL_3 );
    }

    /** {@inheritDoc} */
    public void section4()
    {
        onSection( SECTION_LEVEL_4, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle4()
    {
        onSectionTitle( SECTION_LEVEL_4, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle4_()
    {
        onSectionTitle_( SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    public void section4_()
    {
        onSection_( SECTION_LEVEL_4 );
    }

    /** {@inheritDoc} */
    public void section5()
    {
        onSection( SECTION_LEVEL_5, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle5()
    {
        onSectionTitle( SECTION_LEVEL_5, null );
    }

    /** {@inheritDoc} */
    public void sectionTitle5_()
    {
        onSectionTitle_( SECTION_LEVEL_5 );
    }

    /** {@inheritDoc} */
    public void section5_()
    {
        onSection_( SECTION_LEVEL_5 );
    }

    /**
     * Starts a section. The default class style is <code>section</code>.
     *
     * @param depth The level of the section.
     * @param attributes some attributes.
     * @see javax.swing.text.html.HTML.Tag#DIV
     */
    protected void onSection( int depth, SinkEventAttributes attributes )
    {
        if ( depth >= SECTION_LEVEL_1 && depth <= SECTION_LEVEL_5 )
        {
            MutableAttributeSet att = new SinkEventAttributeSet();
            att.addAttribute( Attribute.CLASS, "section" );
            // NOTE: any class entry in attributes will overwrite the above
            att.addAttributes( SinkUtils.filterAttributes(
                    attributes, SinkUtils.SINK_BASE_ATTRIBUTES  ) );

            att.removeAttribute( Attribute.ID.toString() );
            writeStartTag( Tag.DIV, att );
        }
    }

    /**
     * Ends a section.
     *
     * @param depth The level of the section.
     * @see javax.swing.text.html.HTML.Tag#DIV
     */
    protected void onSection_( int depth )
    {
        if ( depth >= SECTION_LEVEL_1 && depth <= SECTION_LEVEL_5 )
        {
            writeEndTag( Tag.DIV );
        }
    }

    /**
     * Starts a section title.
     *
     * @param depth The level of the section title.
     * @param attributes some attributes.
     * @see javax.swing.text.html.HTML.Tag#H2
     * @see javax.swing.text.html.HTML.Tag#H3
     * @see javax.swing.text.html.HTML.Tag#H4
     * @see javax.swing.text.html.HTML.Tag#H5
     * @see javax.swing.text.html.HTML.Tag#H6
     */
    protected void onSectionTitle( int depth, SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        if ( depth == SECTION_LEVEL_1 )
        {
            writeStartTag( Tag.H2, atts );
        }
        else if ( depth == SECTION_LEVEL_2 )
        {
            writeStartTag( Tag.H3, atts );
        }
        else if ( depth == SECTION_LEVEL_3 )
        {
            writeStartTag( Tag.H4, atts );
        }
        else if ( depth == SECTION_LEVEL_4 )
        {
            writeStartTag( Tag.H5, atts );
        }
        else if ( depth == SECTION_LEVEL_5 )
        {
            writeStartTag( Tag.H6, atts );
        }
    }

    /**
     * Ends a section title.
     *
     * @param depth The level of the section title.
     * @see javax.swing.text.html.HTML.Tag#H2
     * @see javax.swing.text.html.HTML.Tag#H3
     * @see javax.swing.text.html.HTML.Tag#H4
     * @see javax.swing.text.html.HTML.Tag#H5
     * @see javax.swing.text.html.HTML.Tag#H6
     */
    protected void onSectionTitle_( int depth )
    {
        if ( depth == SECTION_LEVEL_1 )
        {
            writeEndTag( Tag.H2 );
        }
        else if ( depth == SECTION_LEVEL_2 )
        {
            writeEndTag( Tag.H3 );
        }
        else if ( depth == SECTION_LEVEL_3 )
        {
            writeEndTag( Tag.H4 );
        }
        else if ( depth == SECTION_LEVEL_4 )
        {
            writeEndTag( Tag.H5 );
        }
        else if ( depth == SECTION_LEVEL_5 )
        {
            writeEndTag( Tag.H6 );
        }
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#UL
     */
    public void list()
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        writeStartTag( Tag.UL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#UL
     */
    public void list( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( Tag.UL, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#UL
     */
    public void list_()
    {
        writeEndTag( Tag.UL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void listItem()
    {
        writeStartTag( Tag.LI );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void listItem( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( Tag.LI, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void listItem_()
    {
        writeEndTag( Tag.LI );
    }

    /**
     * The default list style depends on the numbering.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#OL
     */
    public void numberedList( int numbering )
    {
        numberedList( numbering, null );
    }

    /**
     * The default list style depends on the numbering.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#OL
     */
    public void numberedList( int numbering, SinkEventAttributes attributes )
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        String style;
        switch ( numbering )
        {
            case NUMBERING_UPPER_ALPHA:
                style = "upper-alpha";
                break;
            case NUMBERING_LOWER_ALPHA:
                style = "lower-alpha";
                break;
            case NUMBERING_UPPER_ROMAN:
                style = "upper-roman";
                break;
            case NUMBERING_LOWER_ROMAN:
                style = "lower-roman";
                break;
            case NUMBERING_DECIMAL:
            default:
                style = "decimal";
        }

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        if ( atts == null )
        {
            atts = new SinkEventAttributeSet( 1 );
        }

        atts.addAttribute( Attribute.STYLE, "list-style-type: " + style );

        writeStartTag( Tag.OL, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#OL
     */
    public void numberedList_()
    {
        writeEndTag( Tag.OL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void numberedListItem()
    {
        writeStartTag( Tag.LI );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void numberedListItem( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( Tag.LI, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#LI
     */
    public void numberedListItem_()
    {
        writeEndTag( Tag.LI );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DL
     */
    public void definitionList()
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        writeStartTag( Tag.DL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DL
     */
    public void definitionList( SinkEventAttributes attributes )
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( Tag.DL, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DL
     */
    public void definitionList_()
    {
        writeEndTag( Tag.DL );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DT
     */
    public void definedTerm( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( Tag.DT, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DT
     */
    public void definedTerm()
    {
        writeStartTag( Tag.DT );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DT
     */
    public void definedTerm_()
    {
        writeEndTag( Tag.DT );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DD
     */
    public void definition()
    {
        writeStartTag( Tag.DD );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DD
     */
    public void definition( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        writeStartTag( Tag.DD, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DD
     */
    public void definition_()
    {
        writeEndTag( Tag.DD );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#IMG
     * @deprecated Use {@link #figure(SinkEventAttributes)}, this method is only kept for
     * backward compatibility. Note that the behavior is different though, as this method
     * writes an img tag, while correctly the img tag should be written by  figureGraphics().
     */
    public void figure()
    {
        write( String.valueOf( LESS_THAN ) + Tag.IMG );
        legacyFigure = true;
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#IMG
     */
    public void figure( SinkEventAttributes attributes )
    {
        inFigure = true;

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        if ( atts == null )
        {
            atts = new SinkEventAttributeSet( 1 );
        }

        if ( !atts.isDefined( SinkEventAttributes.CLASS ) )
        {
            atts.addAttribute( SinkEventAttributes.CLASS, "figure" );
        }

        writeStartTag( Tag.DIV, atts );
    }

    /** {@inheritDoc} */
    public void figure_()
    {
        if ( legacyFigure )
        {
            if ( !figureCaptionFlag )
            {
                // Attribute "alt" is required and must be specified for element type "img".
                write( String.valueOf( SPACE ) + Attribute.ALT + EQUAL + QUOTE + QUOTE );
            }
            write( String.valueOf( SPACE ) + SLASH + GREATER_THAN );
            legacyFigure = false;
        }
        else
        {
            writeEndTag( Tag.DIV );
            inFigure = false;
        }

        figureCaptionFlag = false;
    }

    /** {@inheritDoc}
     * @deprecated Use {@link #figureGraphics(String,SinkEventAttributes)},
     * this method is only kept for backward compatibility. Note that the behavior is
     * different though, as this method does not write the img tag, only the src attribute.
     */
    public void figureGraphics( String name )
    {
        write( String.valueOf( SPACE ) + Attribute.SRC + EQUAL + QUOTE + name + QUOTE );
    }

    /** {@inheritDoc} */
    public void figureGraphics( String src, SinkEventAttributes attributes )
    {
        if ( inFigure )
        {
            MutableAttributeSet atts = new SinkEventAttributeSet( 1 );
            atts.addAttribute( SinkEventAttributes.ALIGN, "center" );

            writeStartTag( Tag.P, atts );
        }

        int count = ( attributes == null ? 1 : attributes.getAttributeCount() + 1 );

        MutableAttributeSet atts = new SinkEventAttributeSet( count );

        atts.addAttribute( Attribute.SRC, src );
        atts.addAttributes( SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_IMG_ATTRIBUTES ) );
        if ( atts.getAttribute( Attribute.ALT ) == null )
        {
            atts.addAttribute( Attribute.ALT, "" );
        }

        writeStartTag( Tag.IMG, atts, true );

        if ( inFigure )
        {
            writeEndTag( Tag.P );
        }
    }

    /** {@inheritDoc}
     * @deprecated Use {@link #figureCaption(SinkEventAttributes)},
     * this method is only kept for backward compatibility. Note that the behavior is
     * different though, as this method only writes an alt attribute.
     */
    public void figureCaption()
    {
        figureCaptionFlag = true;
        write( String.valueOf( SPACE ) + Attribute.ALT + EQUAL + QUOTE );
        legacyFigureCaption = true;
    }

    /** {@inheritDoc} */
    public void figureCaption( SinkEventAttributes attributes )
    {
        if ( legacyFigureCaption )
        {
            write( String.valueOf( SPACE ) + Attribute.ALT + EQUAL + QUOTE );
            legacyFigureCaption = false;
            figureCaptionFlag = true;
        }
        else
        {
            SinkEventAttributeSet atts = new SinkEventAttributeSet( 1 );
            atts.addAttribute( SinkEventAttributes.ALIGN, "center" );
            atts.addAttributes( SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  ) );

            paragraph( atts );
            italic();
        }
    }

    /** {@inheritDoc} */
    public void figureCaption_()
    {
        if ( legacyFigureCaption )
        {
            write( String.valueOf( QUOTE ) );
        }
        else
        {
            italic_();
            paragraph_();
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#P
     */
    public void paragraph()
    {
        paragraphFlag = true;

        writeStartTag( Tag.P );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#P
     */
    public void paragraph( SinkEventAttributes attributes )
    {
        paragraphFlag = true;

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( Tag.P, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#P
     */
    public void paragraph_()
    {
        if ( paragraphFlag )
        {
            writeEndTag( Tag.P );
            paragraphFlag = false;
        }
    }

    /**
     * The default class style for boxed is <code>source</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DIV
     * @see javax.swing.text.html.HTML.Tag#PRE
     */
    public void verbatim( boolean boxed )
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        SinkEventAttributeSet att = new SinkEventAttributeSet();

        if ( boxed )
        {
            att.addAttribute( SinkEventAttributes.DECORATION, "boxed" );
        }

        verbatim( att );
    }

    /**
     * The default class style for boxed is <code>source</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DIV
     * @see javax.swing.text.html.HTML.Tag#PRE
     */
    public void verbatim( SinkEventAttributes attributes )
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        verbatimFlag = true;

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_VERBATIM_ATTRIBUTES  );

        if ( atts == null )
        {
            atts = new SinkEventAttributeSet();
        }

        boolean boxed = false;

        if ( atts.isDefined( SinkEventAttributes.DECORATION ) )
        {
            boxed =
                "boxed".equals( atts.getAttribute( SinkEventAttributes.DECORATION ).toString() );
        }

        if ( boxed )
        {
            atts.addAttribute( Attribute.CLASS, "source" );
        }

        atts.removeAttribute( SinkEventAttributes.DECORATION );

        String width = (String) atts.getAttribute( Attribute.WIDTH.toString() );
        atts.removeAttribute( Attribute.WIDTH.toString() );

        writeStartTag( Tag.DIV, atts );

        if ( width != null )
        {
            atts.addAttribute( Attribute.WIDTH.toString(), width );
        }

        atts.removeAttribute( Attribute.ALIGN.toString() );
        atts.removeAttribute( Attribute.CLASS.toString() );

        writeStartTag( Tag.PRE, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#DIV
     * @see javax.swing.text.html.HTML.Tag#PRE
     */
    public void verbatim_()
    {
        writeEndTag( Tag.PRE );
        writeEndTag( Tag.DIV );

        verbatimFlag = false;

    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#HR
     */
    public void horizontalRule()
    {
        writeSimpleTag( Tag.HR );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#HR
     */
    public void horizontalRule( SinkEventAttributes attributes )
    {
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_HR_ATTRIBUTES  );

        writeSimpleTag( Tag.HR, atts );
    }

    /** {@inheritDoc} */
    public void table()
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        // start table with tableRows
        table( null );
    }

    /** {@inheritDoc} */
    public void table( SinkEventAttributes attributes )
    {
        if ( paragraphFlag )
        {
            // The content of element type "p" must match
            // "(a|br|span|bdo|object|applet|img|map|iframe|tt|i|b|u|s|strike|big|small|font|basefont|em|strong|
            // dfn|code|q|samp|kbd|var|cite|abbr|acronym|sub|sup|input|select|textarea|label|button|ins|del|script)".
            paragraph_();
        }

        tableWriter = new StringWriter();

        // start table with tableRows
        if ( attributes == null )
        {
            this.tableAttributes = new SinkEventAttributeSet( 0 );
        }
        else
        {
            this.tableAttributes = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_TABLE_ATTRIBUTES  );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TABLE
     */
    public void table_()
    {
        writeEndTag( Tag.TABLE );

        if ( tableWriter == null )
        {
            throw new IllegalArgumentException( "table( SinkEventAttributes attributes ) was not called before." );
        }

        String content = tableWriter.toString();
        tableWriter = null;

        String startCaption = "<" + Tag.CAPTION.toString() + ">";
        String endCaption = "</" + Tag.CAPTION.toString() + ">";

        if ( content.indexOf( startCaption ) == -1 && content.indexOf( endCaption ) == -1 )
        {
            write( content );
        }
        else
        {
            // DOXIA-177
            int iStartCaption = content.indexOf( startCaption );
            int iEndCaption = content.indexOf( endCaption ) + endCaption.length();

            String captionTag = content.substring( iStartCaption, iEndCaption );
            content = StringUtils.replace( content, captionTag, "" );

            StringBuffer text = new StringBuffer();
            text.append( content.substring( 0, content.indexOf( "<" + Tag.TR.toString() ) ) );
            text.append( captionTag );
            text.append( content.substring( content.indexOf( "<" + Tag.TR.toString() ) ) );

            write( text.toString() );
        }
    }

    /**
     * The default class style is <code>bodyTable</code>.
     * The default align is <code>center</code>.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TABLE
     */
    public void tableRows( int[] justification, boolean grid )
    {
        this.cellJustif = justification;
        this.isCellJustif = true;

        if ( this.tableAttributes == null )
        {
            this.tableAttributes = new SinkEventAttributeSet( 0 );
        }

        MutableAttributeSet att = new SinkEventAttributeSet();

        if ( !tableAttributes.isDefined( Attribute.ALIGN.toString() ) )
        {
            att.addAttribute( Attribute.ALIGN, "center" );
        }

        if ( !tableAttributes.isDefined( Attribute.BORDER.toString() ) )
        {
            att.addAttribute( Attribute.BORDER, ( grid ? "1" : "0" ) );
        }

        if ( !tableAttributes.isDefined( Attribute.CLASS.toString() ) )
        {
            att.addAttribute( Attribute.CLASS, "bodyTable" );
        }

        att.addAttributes( tableAttributes );

        tableAttributes.removeAttributes( tableAttributes );

        writeStartTag( Tag.TABLE, att );
    }

    /** {@inheritDoc} */
    public void tableRows_()
    {
        this.cellJustif = null;
        this.isCellJustif = false;

        this.evenTableRow = true;
    }

    /**
     * The default class style is <code>a</code> or <code>b</code> depending the row id.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TR
     */
    public void tableRow()
    {
        tableRow( null );
    }

    /**
     * The default class style is <code>a</code> or <code>b</code> depending the row id.
     *
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TR
     */
    public void tableRow( SinkEventAttributes attributes )
    {
        MutableAttributeSet att = new SinkEventAttributeSet();

        if ( evenTableRow )
        {
            att.addAttribute( Attribute.CLASS, "a" );
        }
        else
        {
            att.addAttribute( Attribute.CLASS, "b" );
        }

        att.addAttributes( SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_TR_ATTRIBUTES  ) );

        writeStartTag( Tag.TR, att );

        evenTableRow = !evenTableRow;

        cellCount = 0;
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TR
     */
    public void tableRow_()
    {
        writeEndTag( Tag.TR );

        cellCount = 0;
    }

    /** {@inheritDoc} */
    public void tableCell()
    {
        tableCell( false, null );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell()
    {
        tableCell( true, null );
    }

    /** {@inheritDoc} */
    public void tableCell( String width )
    {
        MutableAttributeSet att = new SinkEventAttributeSet();
        att.addAttribute( Attribute.WIDTH, width );

        tableCell( false, att );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( String width )
    {
        MutableAttributeSet att = new SinkEventAttributeSet();
        att.addAttribute( Attribute.WIDTH, width );

        tableCell( true, att );
    }

    /** {@inheritDoc} */
    public void tableCell( SinkEventAttributes attributes )
    {
        tableCell( false, attributes );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell( SinkEventAttributes attributes )
    {
        tableCell( true, attributes );
    }

    /**
     * @param headerRow true if it is an header row
     * @param attributes the cell attributes
     * @see javax.swing.text.html.HTML.Tag#TH
     * @see javax.swing.text.html.HTML.Tag#TD
     */
    private void tableCell( boolean headerRow, MutableAttributeSet attributes )
    {
        String justif = null;

        if ( cellJustif != null && isCellJustif )
        {
            switch ( cellJustif[Math.min( cellCount, cellJustif.length - 1 )] )
            {
                case Sink.JUSTIFY_LEFT:
                    justif = "left";
                    break;
                case Sink.JUSTIFY_RIGHT:
                    justif = "right";
                    break;
                case Sink.JUSTIFY_CENTER:
                default:
                    justif = "center";
                    break;
            }
        }


        Tag t = ( headerRow ? Tag.TH : Tag.TD );

        MutableAttributeSet att = new SinkEventAttributeSet();

        if ( justif != null )
        {
            att.addAttribute( Attribute.ALIGN, justif );
        }

        att.addAttributes( SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_TD_ATTRIBUTES  ) );

        writeStartTag( t, att );
    }

    /** {@inheritDoc} */
    public void tableCell_()
    {
        tableCell_( false );
    }

    /** {@inheritDoc} */
    public void tableHeaderCell_()
    {
        tableCell_( true );
    }

    /**
     * Ends a table cell.
     *
     * @param headerRow true if it is an header row
     * @see javax.swing.text.html.HTML.Tag#TH
     * @see javax.swing.text.html.HTML.Tag#TD
     */
    private void tableCell_( boolean headerRow )
    {
        Tag t = ( headerRow ? Tag.TH : Tag.TD );

        writeEndTag( t );

        if ( isCellJustif )
        {
            ++cellCount;
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#CAPTION
     */
    public void tableCaption()
    {
        tableCaption( null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#CAPTION
     */
    public void tableCaption( SinkEventAttributes attributes )
    {
        // TODO: tableCaption should be written before tableRows (DOXIA-177)
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_SECTION_ATTRIBUTES  );

        writeStartTag( Tag.CAPTION, atts );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#CAPTION
     */
    public void tableCaption_()
    {
        writeEndTag( Tag.CAPTION );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void anchor( String name )
    {
        anchor( name, null );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void anchor( String name, SinkEventAttributes attributes )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Anchor name cannot be null!" );
        }

        if ( headFlag )
        {
            return;
        }

        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BASE_ATTRIBUTES  );

        String id = name;

        if ( !DoxiaUtils.isValidId( id ) )
        {
            id = DoxiaUtils.encodeId( name );

            getLog().warn( "Modified invalid anchor name: " + name );
        }

        MutableAttributeSet att = new SinkEventAttributeSet();
        att.addAttribute( Attribute.NAME, id );
        att.addAttributes( atts );

        writeStartTag( Tag.A, att );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void anchor_()
    {
        if ( !headFlag )
        {
            writeEndTag( Tag.A );
        }
    }

    /** {@inheritDoc} */
    public void link( String name )
    {
        link( name, null, null );
    }

    /** {@inheritDoc} */
    public void link( String name, SinkEventAttributes attributes )
    {
        String target = (String) attributes.getAttribute( Attribute.TARGET.toString() );
        MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_LINK_ATTRIBUTES  );

        link( name, target, atts );
    }

    /**
     * Adds a link with an optional target.
     * The default style class for external link is <code>externalLink</code>.
     *
     * @param href the link href.
     * @param target the link target, may be null.
     * @param attributes an AttributeSet, may be null.
     *      This is supposed to be filtered already.
     * @see javax.swing.text.html.HTML.Tag#A
     */
    private void link( String href, String target, MutableAttributeSet attributes )
    {
        if ( href == null )
        {
            throw new NullPointerException( "Link name cannot be null!" );
        }

        if ( headFlag )
        {
            return;
        }

        MutableAttributeSet att = new SinkEventAttributeSet();

        if ( DoxiaUtils.isExternalLink( href  ) )
        {
            att.addAttribute( Attribute.CLASS, "externalLink" );
        }

        att.addAttribute( Attribute.HREF, HtmlTools.escapeHTML( href  ) );

        if ( target != null )
        {
            att.addAttribute( Attribute.TARGET, target );
        }

        if ( attributes != null )
        {
            attributes.removeAttribute( Attribute.HREF.toString() );
            attributes.removeAttribute( Attribute.TARGET.toString() );
            att.addAttributes( attributes );
        }

        writeStartTag( Tag.A, att );
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#A
     */
    public void link_()
    {
        if ( !headFlag )
        {
            writeEndTag( Tag.A );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#I
     */
    public void italic()
    {
        if ( !headFlag )
        {
            writeStartTag( Tag.I );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#I
     */
    public void italic_()
    {
        if ( !headFlag )
        {
            writeEndTag( Tag.I );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#B
     */
    public void bold()
    {
        if ( !headFlag )
        {
            writeStartTag( Tag.B );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#B
     */
    public void bold_()
    {
        if ( !headFlag )
        {
            writeEndTag( Tag.B );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TT
     */
    public void monospaced()
    {
        if ( !headFlag )
        {
            writeStartTag( Tag.TT );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#TT
     */
    public void monospaced_()
    {
        if ( !headFlag )
        {
            writeEndTag( Tag.TT );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BR
     */
    public void lineBreak()
    {
        if ( headFlag || isVerbatimFlag() )
        {
            getBuffer().append( EOL );
        }
        else
        {
            writeSimpleTag( Tag.BR );
        }
    }

    /**
     * {@inheritDoc}
     * @see javax.swing.text.html.HTML.Tag#BR
     */
    public void lineBreak( SinkEventAttributes attributes )
    {
        if ( headFlag )
        {
            getBuffer().append( EOL );
        }
        else
        {
            MutableAttributeSet atts = SinkUtils.filterAttributes(
                attributes, SinkUtils.SINK_BR_ATTRIBUTES  );

            writeSimpleTag( Tag.BR, atts );
        }
    }

    /** {@inheritDoc} */
    public void pageBreak()
    {
        comment( "PB" );
    }

    /** {@inheritDoc} */
    public void nonBreakingSpace()
    {
        if ( headFlag )
        {
            getBuffer().append( ' ' );
        }
        else
        {
            write( "&#160;" );
        }
    }

    /** {@inheritDoc} */
    public void text( String text )
    {
        if ( headFlag )
        {
            getBuffer().append( text );
        }
        else if ( verbatimFlag )
        {
            verbatimContent( text );
        }
        else
        {
            content( text );
        }
    }

    public void text ( String text, SinkEventAttributes attributes )
    {
        if ( attributes == null )
        {
            text( text );
        }
        else
        {
            if ( attributes.containsAttribute( SinkEventAttributes.DECORATION, "underline" ) )
            {
                writeStartTag( Tag.U );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.DECORATION, "line-through" ) )
            {
                writeStartTag( Tag.S );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sub" ) )
            {
                writeStartTag( Tag.SUB );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sup" ) )
            {
                writeStartTag( Tag.SUP );
            }

            text( text );

            if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sup" ) )
            {
                writeEndTag( Tag.SUP );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.VALIGN, "sub" ) )
            {
                writeEndTag( Tag.SUB );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.DECORATION, "line-through" ) )
            {
                writeEndTag( Tag.S );
            }
            if ( attributes.containsAttribute( SinkEventAttributes.DECORATION, "underline" ) )
            {
                writeEndTag( Tag.U );
            }
        }
    }

    /** {@inheritDoc} */
    public void rawText( String text )
    {
        write( text );
    }

    /** {@inheritDoc} */
    public void comment( String comment )
    {
        StringBuffer buf = new StringBuffer( comment.length() + 9 );

        buf.append( "" + LESS_THAN + BANG + MINUS + MINUS + SPACE );

        buf.append( comment );

        buf.append( "" + SPACE + MINUS + MINUS + GREATER_THAN );

        rawText( buf.toString() );
    }

    /**
     * Adding an unkown event, <i>ie</i> an event that was not recognized by a parser.
     * If {@link org.apache.maven.doxia.util.HtmlTools#getHtmlTag(String) HtmlTools.getHtmlTag( name )}
     * does not return null, the corresponding tag will be written.
     *
     * @param name The name of the event.
     * @param requiredParams If <code>name</code> is a defined HTML tag, the first element of
     * this array has to be an Integer wrapping one of the TAG_TYPE constants in
     * {@link org.apache.maven.doxia.markup.HtmlMarkup HtmlMarkup}, otherwise an
     * IllegalArgumentException is thrown.
     * @param attributes A set of optional {@link SinkEventAttributes}, may be <code>null</code>.
     */
    public void unknown( String name, Object[] requiredParams, SinkEventAttributes attributes )
    {
        Tag tag = HtmlTools.getHtmlTag( name );

        if ( tag == null )
        {
            getLog().warn( "No HTML tag found for unknown Sink event: " + name + ", ignoring!" );
        }
        else
        {
            if ( requiredParams == null || !( requiredParams[0] instanceof Integer ) )
            {
                throw new IllegalArgumentException( "Missing required parameter: TAG_TYPE" );
            }

            int tagType = ( (Integer) requiredParams[0] ).intValue();

            if ( tagType == TAG_TYPE_SIMPLE )
            {
                writeSimpleTag( tag, attributes );
            }
            else if ( tagType == TAG_TYPE_START )
            {
                writeStartTag( tag, attributes );
            }
            else if ( tagType == TAG_TYPE_END )
            {
                writeEndTag( tag );
            }
            else
            {
                throw new IllegalArgumentException( "Not a valid TAG_TYPE: " + tagType );
            }
        }
    }

    /** {@inheritDoc} */
    public void flush()
    {
        writer.flush();
    }

    /** {@inheritDoc} */
    public void close()
    {
        writer.close();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * Write HTML escaped text to output.
     *
     * @param text The text to write.
     */
    protected void content( String text )
    {
        write( escapeHTML( text ) );
    }

    /**
     * Write HTML escaped text to output.
     *
     * @param text The text to write.
     */
    protected void verbatimContent( String text )
    {
        write( escapeHTML( text ) );
    }

    /**
     * Forward to HtmlTools.escapeHTML( text ).
     *
     * @param text the String to escape, may be null
     * @return the text escaped, "" if null String input
     * @see org.apache.maven.doxia.util.HtmlTools#escapeHTML(String)
     */
    protected static String escapeHTML( String text )
    {
        return HtmlTools.escapeHTML( text );
    }

    /**
     * Forward to HtmlTools.encodeURL( text ).
     *
     * @param text the String to encode, may be null.
     * @return the text encoded, null if null String input.
     * @see org.apache.maven.doxia.util.HtmlTools#encodeURL(String)
     */
    protected static String encodeURL( String text )
    {
        return HtmlTools.encodeURL( text );
    }

    /** {@inheritDoc} */
    protected void write( String text )
    {
        if ( tableWriter == null )
        {
            writer.write( unifyEOLs( text ) );
        }
        else
        {
            tableWriter.write( unifyEOLs( text ) );
        }
    }

}
