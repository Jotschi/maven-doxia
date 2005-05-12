package org.codehaus.doxia.module.xhtml;

import org.codehaus.doxia.sink.SinkAdapter;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SinkDescriptorReader.java,v 1.1 2004/09/22 00:01:42 jvanzyl Exp $
 */
public class SinkDescriptorReader
    extends SinkAdapter
{
    public Map read( Reader reader )
        throws Exception
    {
        Map directives = new HashMap();

        BufferedReader br = new BufferedReader( reader );

        String line;

        String directive = null;

        StringBuffer directiveBody = new StringBuffer();

        while ( ( line = br.readLine() ) != null )
        {
            line = line.trim();

            if ( line.startsWith( "//" ) || line.startsWith( "{" ) || line.length() == 0 )
            {
                continue;
            }
            else if ( line.startsWith( "*" ) )
            {
                directive = line.substring( 1 );

                int index = directive.indexOf( "{" );
                if ( index >= 0 )
                {
                    if ( index < directive.length() )
                    {
                        line = directive.substring( index + 1 );
                    }
                    directive = directive.substring( 0, index ).trim();
                }
                else
                {
                    continue;
                }
            }

            if ( line.endsWith( "}" ) )
            {
                int index = 0;
                while ( index >= 0 && index < line.length() - 1 )
                {
                    index = line.indexOf( "${", index );
                    if ( index >= 0 )
                    {
                        index = line.indexOf( "}", index );
                    }
                }

                if ( index <= 0 )
                {
                    directiveBody.append( line.substring( 0, line.length() - 1 ) );

                    directives.put( directive, directiveBody.toString() );

                    directiveBody = new StringBuffer();
                }
                else
                {
                    directiveBody.append( line ).append( "\n" );
                }
            }
            else
            {
                directiveBody.append( line ).append( "\n" );
            }
        }

        return directives;
    }

    public void head()
    {
    }

    public void head_()
    {
    }

    public void body()
    {
    }

    public void body_()
    {
    }

    public void section1()
    {
    }

    public void section1_()
    {
    }

    public void section2()
    {
    }

    public void section2_()
    {
    }

    public void section3()
    {
    }

    public void section3_()
    {
    }

    public void section4()
    {
    }

    public void section4_()
    {
    }

    public void section5()
    {
    }

    public void section5_()
    {
    }

    public void list()
    {
    }

    public void list_()
    {
    }

    public void listItem()
    {
    }

    public void listItem_()
    {
    }

    public void numberedList( int numbering )
    {
    }

    public void numberedList_()
    {
    }

    public void numberedListItem()
    {
    }

    public void numberedListItem_()
    {
    }

    public void definitionList()
    {
    }

    public void definitionList_()
    {
    }

    public void definitionListItem()
    {
    }

    public void definitionListItem_()
    {
    }

    public void definition()
    {
    }

    public void definition_()
    {
    }

    public void figure()
    {
    }

    public void figure_()
    {
    }

    public void table()
    {
    }

    public void table_()
    {
    }

    public void tableRows( int[] justification, boolean grid )
    {
    }

    public void tableRows_()
    {
    }

    public void tableRow()
    {
    }

    public void tableRow_()
    {
    }

    public void title()
    {
    }

    public void title_()
    {
    }

    public void author()
    {
    }

    public void author_()
    {
    }

    public void date()
    {
    }

    public void date_()
    {
    }

    public void sectionTitle()
    {
    }

    public void sectionTitle_()
    {
    }

    public void paragraph()
    {
    }

    public void paragraph_()
    {
    }

    public void verbatim( boolean boxed )
    {
    }

    public void verbatim_()
    {
    }

    public void definedTerm()
    {
    }

    public void definedTerm_()
    {
    }

    public void figureCaption()
    {
    }

    public void figureCaption_()
    {
    }

    public void tableCell()
    {
    }

    public void tableCell_()
    {
    }

    public void tableHeaderCell()
    {
    }

    public void tableHeaderCell_()
    {
    }

    public void tableCaption()
    {
    }

    public void tableCaption_()
    {
    }

    public void figureGraphics( String name )
    {
    }

    public void horizontalRule()
    {
    }

    public void pageBreak()
    {
    }

    public void anchor( String name )
    {
    }

    public void anchor_()
    {
    }

    public void link( String name )
    {
    }

    public void link_()
    {
    }

    public void italic()
    {
    }

    public void italic_()
    {
    }

    public void bold()
    {
    }

    public void bold_()
    {
    }

    public void monospaced()
    {
    }

    public void monospaced_()
    {
    }

    public void lineBreak()
    {
    }

    public void nonBreakingSpace()
    {
    }

    public void text( String text )
    {
    }
}
