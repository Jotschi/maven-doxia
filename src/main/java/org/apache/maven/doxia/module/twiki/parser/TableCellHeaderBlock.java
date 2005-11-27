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

import org.apache.maven.doxia.sink.Sink;


/**
 * Table Cell headear
 *
 * @author Juan F. Codagnone
 * @since Nov 10, 2005
 */
public class TableCellHeaderBlock extends AbstractFatherBlock
{

    /**
     * Creates the TableCellHeaderBlock.
     *
     * @param childBlocks childrens
     */
    public TableCellHeaderBlock( final Block [] childBlocks )
    {
        super( childBlocks );
    }

    /**
     * @see AbstractFatherBlock#before(org.apache.maven.doxia.sink.Sink)
     */
    @Override
    public final void before( final Sink sink )
    {
        sink.tableHeaderCell();
    }

    /**
     * @see AbstractFatherBlock#after(org.apache.maven.doxia.sink.Sink)
     */
    @Override
    public final void after( final Sink sink )
    {
        sink.tableHeaderCell_();
    }
}
