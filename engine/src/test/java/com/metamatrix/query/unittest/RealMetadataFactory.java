/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package com.metamatrix.query.unittest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.teiid.adminapi.impl.VDBMetaData;
import org.teiid.client.metadata.ParameterInfo;
import org.teiid.connector.metadata.runtime.Column;
import org.teiid.connector.metadata.runtime.ColumnSet;
import org.teiid.connector.metadata.runtime.MetadataStore;
import org.teiid.connector.metadata.runtime.Procedure;
import org.teiid.connector.metadata.runtime.ProcedureParameter;
import org.teiid.connector.metadata.runtime.Schema;
import org.teiid.connector.metadata.runtime.Table;
import org.teiid.connector.metadata.runtime.BaseColumn.NullType;
import org.teiid.connector.metadata.runtime.Column.SearchType;
import org.teiid.connector.metadata.runtime.ProcedureParameter.Type;
import org.teiid.metadata.CompositeMetadataStore;
import org.teiid.metadata.TransformationMetadata;

import com.metamatrix.common.types.DataTypeManager;
import com.metamatrix.query.mapping.relational.QueryNode;
import com.metamatrix.query.sql.lang.SPParameter;

public class RealMetadataFactory {

    private static TransformationMetadata CACHED_BQT = exampleBQT();
        
	private RealMetadataFactory() { }
	
    public static TransformationMetadata exampleBQTCached() {
        return CACHED_BQT;
    }
    
    public static TransformationMetadata exampleBQT() {
    	MetadataStore metadataStore = new MetadataStore();
    	
        Schema bqt1 = createPhysicalModel("BQT1", metadataStore); //$NON-NLS-1$
        Schema bqt2 = createPhysicalModel("BQT2", metadataStore); //$NON-NLS-1$
        Schema bqt3 = createPhysicalModel("BQT3", metadataStore); //$NON-NLS-1$
        Schema lob = createPhysicalModel("LOB", metadataStore); //$NON-NLS-1$
        Schema vqt = createVirtualModel("VQT", metadataStore); //$NON-NLS-1$
        Schema bvqt = createVirtualModel("BQT_V", metadataStore); //$NON-NLS-1$
        Schema bvqt2 = createVirtualModel("BQT2_V", metadataStore); //$NON-NLS-1$
        
        // Create physical groups
        Table bqt1SmallA = createPhysicalGroup("SmallA", bqt1); //$NON-NLS-1$
        Table bqt1SmallB = createPhysicalGroup("SmallB", bqt1); //$NON-NLS-1$
        Table bqt1MediumA = createPhysicalGroup("MediumA", bqt1); //$NON-NLS-1$
        Table bqt1MediumB = createPhysicalGroup("MediumB", bqt1); //$NON-NLS-1$
        Table bqt2SmallA = createPhysicalGroup("SmallA", bqt2); //$NON-NLS-1$
        Table bqt2SmallB = createPhysicalGroup("SmallB", bqt2); //$NON-NLS-1$
        Table bqt2MediumA = createPhysicalGroup("MediumA", bqt2); //$NON-NLS-1$
        Table bqt2MediumB = createPhysicalGroup("MediumB", bqt2); //$NON-NLS-1$
        Table bqt3SmallA = createPhysicalGroup("SmallA", bqt3); //$NON-NLS-1$
        Table bqt3SmallB = createPhysicalGroup("SmallB", bqt3); //$NON-NLS-1$
        Table bqt3MediumA = createPhysicalGroup("MediumA", bqt3); //$NON-NLS-1$
        Table bqt3MediumB = createPhysicalGroup("MediumB", bqt3); //$NON-NLS-1$
        Table lobTable = createPhysicalGroup("LobTbl", lob); //$NON-NLS-1$
        Table library = createPhysicalGroup("LOB_TESTING_ONE", lob); //$NON-NLS-1$
        
        createElements( library, new String[] { "CLOB_COLUMN", "BLOB_COLUMN", "KEY_EMULATOR" }, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        		new String[] { DataTypeManager.DefaultDataTypes.CLOB, DataTypeManager.DefaultDataTypes.BLOB, DataTypeManager.DefaultDataTypes.INTEGER }); 

        // Create virtual groups
        QueryNode vqtn1 = new QueryNode("VQT.SmallA", "SELECT * FROM BQT1.SmallA"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg1 = createUpdatableVirtualGroup("SmallA", vqt, vqtn1); //$NON-NLS-1$
        
        QueryNode vqtn2 = new QueryNode("VQT.SmallB", "SELECT Concat(stringKey, stringNum) as a12345 FROM BQT1.SmallA"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg2 = createUpdatableVirtualGroup("SmallB", vqt, vqtn2); //$NON-NLS-1$        

        // Case 2589
        QueryNode vqtn2589 = new QueryNode("VQT.SmallA_2589", "SELECT * FROM BQT1.SmallA WHERE StringNum = '10'"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg2589 = createVirtualGroup("SmallA_2589", vqt, vqtn2589); //$NON-NLS-1$

        QueryNode vqtn2589a = new QueryNode("VQT.SmallA_2589a", "SELECT BQT1.SmallA.* FROM BQT1.SmallA INNER JOIN BQT1.SmallB ON SmallA.IntKey = SmallB.IntKey WHERE SmallA.StringNum = '10'"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg2589a = createVirtualGroup("SmallA_2589a", vqt, vqtn2589a); //$NON-NLS-1$

        QueryNode vqtn2589b = new QueryNode("VQT.SmallA_2589b", "SELECT BQT1.SmallA.* FROM BQT1.SmallA INNER JOIN BQT1.SmallB ON SmallA.StringKey = SmallB.StringKey WHERE SmallA.StringNum = '10'"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg2589b = createVirtualGroup("SmallA_2589b", vqt, vqtn2589b); //$NON-NLS-1$

        QueryNode vqtn2589c = new QueryNode("VQT.SmallA_2589c", "SELECT BQT1.SmallA.* FROM BQT1.SmallA INNER JOIN BQT1.SmallB ON SmallA.StringKey = SmallB.StringKey WHERE concat(SmallA.StringNum, SmallB.StringNum) = '1010'"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg2589c = createVirtualGroup("SmallA_2589c", vqt, vqtn2589c); //$NON-NLS-1$
        
        QueryNode vqtn2589d = new QueryNode("VQT.SmallA_2589d", "SELECT BQT1.SmallA.* FROM BQT1.SmallA INNER JOIN BQT1.SmallB ON SmallA.StringKey = SmallB.StringKey WHERE SmallA.StringNum = '10' AND SmallA.IntNum = 10"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg2589d = createVirtualGroup("SmallA_2589d", vqt, vqtn2589d); //$NON-NLS-1$

        QueryNode vqtn2589f = new QueryNode("VQT.SmallA_2589f", "SELECT * FROM VQT.SmallA_2589"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg2589f = createVirtualGroup("SmallA_2589f", vqt, vqtn2589f); //$NON-NLS-1$

        QueryNode vqtn2589g = new QueryNode("VQT.SmallA_2589g", "SELECT * FROM VQT.SmallA_2589b"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg2589g = createVirtualGroup("SmallA_2589g", vqt, vqtn2589g); //$NON-NLS-1$

        QueryNode vqtn2589h = new QueryNode("VQT.SmallA_2589h", "SELECT VQT.SmallA_2589.* FROM VQT.SmallA_2589 INNER JOIN BQT1.SmallB ON VQT.SmallA_2589.StringKey = SmallB.StringKey"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg2589h = createVirtualGroup("SmallA_2589h", vqt, vqtn2589h); //$NON-NLS-1$
        
        QueryNode vqtn2589i = new QueryNode("VQT.SmallA_2589i", "SELECT BQT1.SmallA.* FROM BQT1.SmallA INNER JOIN BQT1.SmallB ON SmallA.StringKey = SmallB.StringKey WHERE SmallA.StringNum = '10' AND SmallB.StringNum = '10'"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg2589i = createVirtualGroup("SmallA_2589i", vqt, vqtn2589i); //$NON-NLS-1$

        // defect 15355
        QueryNode vqtn15355  = new QueryNode("VQT.Defect15355", "SELECT convert(IntKey, string) as StringKey, BigIntegerValue FROM BQT1.SmallA UNION SELECT StringKey, (SELECT BigIntegerValue FROM BQT3.SmallA WHERE BQT3.SmallA.BigIntegerValue = BQT2.SmallA.StringNum) FROM BQT2.SmallA"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg15355  = createVirtualGroup("Defect15355", vqt, vqtn15355); //$NON-NLS-1$
        QueryNode vqtn15355a  = new QueryNode("VQT.Defect15355a", "SELECT StringKey, StringNum, BigIntegerValue FROM BQT1.SmallA UNION SELECT StringKey, StringNum, (SELECT BigIntegerValue FROM BQT3.SmallA WHERE BQT3.SmallA.BigIntegerValue = BQT2.SmallA.StringNum) FROM BQT2.SmallA"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg15355a  = createVirtualGroup("Defect15355a", vqt, vqtn15355a); //$NON-NLS-1$
        QueryNode vqtn15355b  = new QueryNode("VQT.Defect15355b", "SELECT convert(IntKey, string) as IntKey, BigIntegerValue FROM BQT1.SmallA UNION SELECT StringKey, (SELECT BigIntegerValue FROM BQT3.SmallA WHERE BQT3.SmallA.BigIntegerValue = BQT2.SmallA.StringNum) FROM BQT2.SmallA"); //$NON-NLS-1$ //$NON-NLS-2$
        Table vqtg15355b  = createVirtualGroup("Defect15355b", vqt, vqtn15355b); //$NON-NLS-1$
        
        QueryNode bvqtn1 = new QueryNode("BQT_V.BQT_V", "SELECT a.* FROM BQT1.SMALLA AS a WHERE a.INTNUM = (SELECT MIN(b.INTNUM) FROM BQT1.SMALLA AS b WHERE b.INTKEY = a.IntKey ) OPTION MAKEDEP a"); //$NON-NLS-1$ //$NON-NLS-2$
        Table bvqtg1 = createUpdatableVirtualGroup("BQT_V", bvqt, bvqtn1); //$NON-NLS-1$
        QueryNode bvqt2n1 = new QueryNode("BQT2_V.BQT2_V", "SELECT BQT2.SmallA.* FROM BQT2.SmallA, BQT_V.BQT_V WHERE BQT2.SmallA.IntKey = BQT_V.BQT_V.IntKey"); //$NON-NLS-1$ //$NON-NLS-2$
        Table bvqt2g1 = createUpdatableVirtualGroup("BQT2_V", bvqt2, bvqt2n1); //$NON-NLS-1$

     // Create physical elements
        String[] elemNames = new String[] { 
             "IntKey", "StringKey",  //$NON-NLS-1$ //$NON-NLS-2$
             "IntNum", "StringNum",  //$NON-NLS-1$ //$NON-NLS-2$
             "FloatNum", "LongNum",  //$NON-NLS-1$ //$NON-NLS-2$
             "DoubleNum", "ByteNum",  //$NON-NLS-1$ //$NON-NLS-2$
             "DateValue", "TimeValue",  //$NON-NLS-1$ //$NON-NLS-2$
             "TimestampValue", "BooleanValue",  //$NON-NLS-1$ //$NON-NLS-2$
             "CharValue", "ShortValue",  //$NON-NLS-1$ //$NON-NLS-2$
             "BigIntegerValue", "BigDecimalValue",  //$NON-NLS-1$ //$NON-NLS-2$
             "ObjectValue" }; //$NON-NLS-1$
         String[] elemTypes = new String[] { DataTypeManager.DefaultDataTypes.INTEGER, DataTypeManager.DefaultDataTypes.STRING, 
                             DataTypeManager.DefaultDataTypes.INTEGER, DataTypeManager.DefaultDataTypes.STRING, 
                             DataTypeManager.DefaultDataTypes.FLOAT, DataTypeManager.DefaultDataTypes.LONG, 
                             DataTypeManager.DefaultDataTypes.DOUBLE, DataTypeManager.DefaultDataTypes.BYTE, 
                             DataTypeManager.DefaultDataTypes.DATE, DataTypeManager.DefaultDataTypes.TIME, 
                             DataTypeManager.DefaultDataTypes.TIMESTAMP, DataTypeManager.DefaultDataTypes.BOOLEAN, 
                             DataTypeManager.DefaultDataTypes.CHAR, DataTypeManager.DefaultDataTypes.SHORT, 
                             DataTypeManager.DefaultDataTypes.BIG_INTEGER, DataTypeManager.DefaultDataTypes.BIG_DECIMAL, 
                             DataTypeManager.DefaultDataTypes.OBJECT };
        
        List<Column> bqt1SmallAe = createElements(bqt1SmallA, elemNames, elemTypes);
        bqt1SmallAe.get(1).setNativeType("char"); //$NON-NLS-1$
        List bqt1SmallBe = createElements(bqt1SmallB, elemNames, elemTypes);
        List bqt1MediumAe = createElements(bqt1MediumA, elemNames, elemTypes);
        List bqt1MediumBe = createElements(bqt1MediumB, elemNames, elemTypes);
        List bqt2SmallAe = createElements(bqt2SmallA, elemNames, elemTypes);
        List bqt2SmallBe = createElements(bqt2SmallB, elemNames, elemTypes);
        List bqt2MediumAe = createElements(bqt2MediumA, elemNames, elemTypes);
        List bqt2MediumBe = createElements(bqt2MediumB, elemNames, elemTypes);                
        List bqt3SmallAe = createElements(bqt3SmallA, elemNames, elemTypes);
        List bqt3SmallBe = createElements(bqt3SmallB, elemNames, elemTypes);
        List bqt3MediumAe = createElements(bqt3MediumA, elemNames, elemTypes);
        List bqt3MediumBe = createElements(bqt3MediumB, elemNames, elemTypes);
        List lobTable_elem = createElements(lobTable, new String[] {"ClobValue"}, new String[] {DataTypeManager.DefaultDataTypes.CLOB}); //$NON-NLS-1$
        
        // Create virtual elements
        List vqtg1e = createElements(vqtg1, elemNames, elemTypes);        
        List vqtg2e = createElements(vqtg2, new String[] {"a12345"}, new String[] {DataTypeManager.DefaultDataTypes.STRING});  //$NON-NLS-1$
        List vqtg15355e = createElements(vqtg15355, new String[] {"StringKey", "BigIntegerValue"}, new String[] {DataTypeManager.DefaultDataTypes.STRING, DataTypeManager.DefaultDataTypes.BIG_INTEGER});         //$NON-NLS-1$ //$NON-NLS-2$
        List vqtg15355ae = createElements(vqtg15355a, new String[] {"StringKey", "StringNum", "BigIntegerValue"}, new String[] {DataTypeManager.DefaultDataTypes.STRING, DataTypeManager.DefaultDataTypes.STRING, DataTypeManager.DefaultDataTypes.BIG_INTEGER});         //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        List vqtg15355be = createElements(vqtg15355b, new String[] {"IntKey", "BigIntegerValue"}, new String[] {DataTypeManager.DefaultDataTypes.STRING, DataTypeManager.DefaultDataTypes.BIG_INTEGER});         //$NON-NLS-1$ //$NON-NLS-2$
        List vqtg2589e = createElements(vqtg2589, elemNames, elemTypes);        
        List vqtg2589ae = createElements(vqtg2589a, elemNames, elemTypes);        
        List vqtg2589be = createElements(vqtg2589b, elemNames, elemTypes);        
        List vqtg2589ce = createElements(vqtg2589c, elemNames, elemTypes);        
        List vqtg2589de = createElements(vqtg2589d, elemNames, elemTypes);        
        List vqtg2589fe = createElements(vqtg2589f, elemNames, elemTypes);        
        List vqtg2589ge = createElements(vqtg2589g, elemNames, elemTypes);        
        List vqtg2589he = createElements(vqtg2589h, elemNames, elemTypes);        
        List vqtg2589ie = createElements(vqtg2589i, elemNames, elemTypes);
        List bvqtg1e = createElements(bvqtg1, elemNames, elemTypes);        
        List bvqt2g1e = createElements(bvqt2g1, elemNames, elemTypes);        

     // Add stored procedure
        Schema pm1 = createPhysicalModel("pm1", metadataStore); //$NON-NLS-1$
        ProcedureParameter rs1p1 = createParameter("intkey", 2, ParameterInfo.IN, DataTypeManager.DefaultDataTypes.INTEGER, null);         //$NON-NLS-1$
        ColumnSet<Procedure> rs1 = createResultSet("rs1", pm1, new String[] { "IntKey", "StringKey" }, new String[] { DataTypeManager.DefaultDataTypes.INTEGER, DataTypeManager.DefaultDataTypes.STRING }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Procedure spTest5 = createStoredProcedure("spTest5", pm1, Arrays.asList(rs1p1), "spTest5"); //$NON-NLS-1$ //$NON-NLS-2$
        spTest5.setResultSet(rs1);

        Schema pm2 = createPhysicalModel("pm2", metadataStore); //$NON-NLS-1$
        ProcedureParameter rs2p1 = createParameter("inkey", 2, ParameterInfo.IN, DataTypeManager.DefaultDataTypes.INTEGER, null); //$NON-NLS-1$
        ProcedureParameter rs2p2 = createParameter("outkey", 3, ParameterInfo.OUT, DataTypeManager.DefaultDataTypes.INTEGER, null);                 //$NON-NLS-1$
        ColumnSet<Procedure> rs2 = createResultSet("rs2", pm2, new String[] { "IntKey", "StringKey"}, new String[] { DataTypeManager.DefaultDataTypes.INTEGER , DataTypeManager.DefaultDataTypes.STRING }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Procedure spTest8 = createStoredProcedure("spTest8", pm2, Arrays.asList(rs2p1, rs2p2), "spTest8"); //$NON-NLS-1$ //$NON-NLS-2$
        spTest8.setResultSet(rs2);
        
        ProcedureParameter rs2p2a = createParameter("outkey", 3, ParameterInfo.OUT, DataTypeManager.DefaultDataTypes.INTEGER, null);                 //$NON-NLS-1$
        ColumnSet<Procedure> rs2a = createResultSet("rs2", pm2, new String[] { "IntKey", "StringKey"}, new String[] { DataTypeManager.DefaultDataTypes.INTEGER , DataTypeManager.DefaultDataTypes.STRING }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Procedure spTest8a = createStoredProcedure("spTest8a", pm2, Arrays.asList(rs2p2a), "spTest8a"); //$NON-NLS-1$ //$NON-NLS-2$
        spTest8a.setResultSet(rs2a);
        
        Schema pm4 = createPhysicalModel("pm4", metadataStore); //$NON-NLS-1$
        ProcedureParameter rs4p1 = createParameter("inkey", 2, ParameterInfo.IN, DataTypeManager.DefaultDataTypes.INTEGER, null); //$NON-NLS-1$
        ProcedureParameter rs4p2 = createParameter("ret", 1, ParameterInfo.RETURN_VALUE, DataTypeManager.DefaultDataTypes.INTEGER, null);  //$NON-NLS-1$
        Procedure spTest9 = createStoredProcedure("spTest9", pm4, Arrays.asList(rs4p1, rs4p2), "spTest9"); //$NON-NLS-1$ //$NON-NLS-2$
        
        Schema pm3 = createPhysicalModel("pm3", metadataStore); //$NON-NLS-1$
        ProcedureParameter rs3p1 = createParameter("inkey", 2, ParameterInfo.IN, DataTypeManager.DefaultDataTypes.INTEGER, null); //$NON-NLS-1$
        ProcedureParameter rs3p2 = createParameter("outkey", 3, ParameterInfo.INOUT, DataTypeManager.DefaultDataTypes.INTEGER, null);                 //$NON-NLS-1$
        ColumnSet<Procedure> rs3 = createResultSet("rs3", pm3, new String[] { "IntKey", "StringKey"}, new String[] { DataTypeManager.DefaultDataTypes.INTEGER , DataTypeManager.DefaultDataTypes.STRING }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Procedure spTest11 = createStoredProcedure("spTest11", pm3, Arrays.asList(rs3p1, rs3p2), "spTest11"); //$NON-NLS-1$ //$NON-NLS-2$
        spTest11.setResultSet(rs3);
        
        //add virtual stored procedures 
        Schema mmspTest1 = createVirtualModel("mmspTest1", metadataStore); //$NON-NLS-1$
        ColumnSet<Procedure> vsprs1 = createResultSet("mmspTest1.vsprs1", mmspTest1, new String[] { "StringKey" }, new String[] { DataTypeManager.DefaultDataTypes.STRING }); //$NON-NLS-1$ //$NON-NLS-2$
        QueryNode vspqn1 = new QueryNode("vsp1", "CREATE VIRTUAL PROCEDURE BEGIN DECLARE integer x; LOOP ON (SELECT intkey FROM bqt1.smallA) AS intKeyCursor BEGIN x= intKeyCursor.intkey - 1; END SELECT stringkey FROM bqt1.smalla where intkey=x; END"); //$NON-NLS-1$ //$NON-NLS-2$
        Procedure vsp1 = createVirtualProcedure("MMSP1", mmspTest1, null, vspqn1); //$NON-NLS-1$
        vsp1.setResultSet(vsprs1);

        ColumnSet<Procedure> vsprs2 = createResultSet("mmspTest1.vsprs1", mmspTest1, new String[] { "StringKey" }, new String[] { DataTypeManager.DefaultDataTypes.STRING }); //$NON-NLS-1$ //$NON-NLS-2$
        QueryNode vspqn2 = new QueryNode("vsp2", "CREATE VIRTUAL PROCEDURE BEGIN DECLARE integer x; LOOP ON (SELECT intkey FROM bqt1.smallA) AS intKeyCursor1 BEGIN LOOP ON (SELECT intkey FROM bqt1.smallB) AS intKeyCursor2 BEGIN x= intKeyCursor1.intkey - intKeyCursor2.intkey; END END SELECT stringkey FROM bqt1.smalla where intkey=x; END"); //$NON-NLS-1$ //$NON-NLS-2$
        Procedure vsp2 = createVirtualProcedure("MMSP2", mmspTest1, null, vspqn2); //$NON-NLS-1$
        vsp2.setResultSet(vsprs2);

        ColumnSet<Procedure> vsprs3 = createResultSet("mmspTest1.vsprs1", mmspTest1, new String[] { "StringKey" }, new String[] { DataTypeManager.DefaultDataTypes.STRING }); //$NON-NLS-1$ //$NON-NLS-2$
        QueryNode vspqn3 = new QueryNode("vsp3", "CREATE VIRTUAL PROCEDURE BEGIN DECLARE integer x; LOOP ON (SELECT intkey FROM bqt1.smallA) AS intKeyCursor BEGIN x= intKeyCursor.intkey - 1; if(x = 25) BEGIN BREAK; END ELSE BEGIN CONTINUE; END END SELECT stringkey FROM bqt1.smalla where intkey=x; END"); //$NON-NLS-1$ //$NON-NLS-2$
        Procedure vsp3 = createVirtualProcedure("MMSP3", mmspTest1, null, vspqn3); //$NON-NLS-1$
        vsp3.setResultSet(vsprs3);

        ColumnSet<Procedure> vsprs4 = createResultSet("mmspTest1.vsprs1", mmspTest1, new String[] { "StringKey" }, new String[] { DataTypeManager.DefaultDataTypes.STRING }); //$NON-NLS-1$ //$NON-NLS-2$
        QueryNode vspqn4 = new QueryNode("vsp4", "CREATE VIRTUAL PROCEDURE BEGIN DECLARE integer x; x=0; WHILE(x < 50) BEGIN x= x + 1; if(x = 25) BEGIN BREAK; END ELSE BEGIN CONTINUE; END END SELECT stringkey FROM bqt1.smalla where intkey=x; END"); //$NON-NLS-1$ //$NON-NLS-2$
        Procedure vsp4 = createVirtualProcedure("MMSP4", mmspTest1, null, vspqn4); //$NON-NLS-1$
        vsp4.setResultSet(vsprs4);
        
        ColumnSet<Procedure> vsprs5 = createResultSet("mmspTest1.vsprs1", mmspTest1, new String[] { "StringKey" }, new String[] { DataTypeManager.DefaultDataTypes.STRING }); //$NON-NLS-1$ //$NON-NLS-2$
        ProcedureParameter vsp5p1 = createParameter("param1", 2, ParameterInfo.IN, DataTypeManager.DefaultDataTypes.STRING, null); //$NON-NLS-1$
        QueryNode vspqn5 = new QueryNode("vsp5", "CREATE VIRTUAL PROCEDURE BEGIN SELECT 0; END"); //$NON-NLS-1$ //$NON-NLS-2$
        Procedure vsp5 = createVirtualProcedure("MMSP5", mmspTest1, Arrays.asList(vsp5p1), vspqn5); //$NON-NLS-1$
        vsp5.setResultSet(vsprs5);

        ColumnSet<Procedure> vsprs6 = createResultSet("mmspTest1.vsprs1", mmspTest1, new String[] { "StringKey" }, new String[] { DataTypeManager.DefaultDataTypes.STRING }); //$NON-NLS-1$ //$NON-NLS-2$
        ProcedureParameter vsp6p1 = createParameter("p1", 2, ParameterInfo.IN, DataTypeManager.DefaultDataTypes.STRING, null); //$NON-NLS-1$
        QueryNode vspqn6 = new QueryNode("vsp6", "CREATE VIRTUAL PROCEDURE BEGIN SELECT 1; END"); //$NON-NLS-1$ //$NON-NLS-2$
        Procedure vsp6 = createVirtualProcedure("MMSP6", mmspTest1, Arrays.asList(vsp6p1), vspqn6); //$NON-NLS-1$
        vsp6.setResultSet(vsprs6);
    	
    	CompositeMetadataStore store = new CompositeMetadataStore(metadataStore);
    	VDBMetaData vdbMetaData = new VDBMetaData();
    	vdbMetaData.setName("bqt"); //$NON-NLS-1$
    	vdbMetaData.setVersion(1);
    	for (Schema schema : metadataStore.getSchemas().values()) {
			vdbMetaData.addModel(FakeMetadataFactory.createModel(schema.getName(), schema.isPhysical()));
		}
    	return new TransformationMetadata(vdbMetaData, store, null, null); 
    }

    /**
     * Create a physical model with default settings.
     */
	public static Schema createPhysicalModel(String name, MetadataStore metadataStore) {
		Schema schema = new Schema();
		schema.setName(name);
		metadataStore.addSchema(schema);
		return schema;	
	}
	
    /**
     * Create a virtual model with default settings.
     */
	public static Schema createVirtualModel(String name, MetadataStore metadataStore) {
		Schema schema = new Schema();
		schema.setName(name);
		schema.setPhysical(false);
		metadataStore.addSchema(schema);
		return schema;	
	}
	
    /**
     * Create a physical group with default settings.
     * @param name Name of physical group, must match model name
     * @param model Associated model
     * @return FakeMetadataObject Metadata object for group
     */
	public static Table createPhysicalGroup(String name, Schema model, boolean fullyQualify) {
		Table table = new Table();
		table.setName(name);
		model.addTable(table);
		table.setSupportsUpdate(true);
		table.setNameInSource((fullyQualify || name.lastIndexOf(".") == -1)? name : name.substring(name.lastIndexOf(".") + 1));  //$NON-NLS-1$ //$NON-NLS-2$
		return table;
	}

    public static Table createPhysicalGroup(String name, Schema model) {
    	return createPhysicalGroup(name, model, false);
    }
    	
    /**
     * Create a virtual group with default settings.
     */
	public static Table createVirtualGroup(String name, Schema model, QueryNode plan) {
        Table table = new Table();
        table.setName(name);
        model.addTable(table);
        table.setVirtual(true);
        table.setSelectTransformation(plan.getQuery());
		return table;
	}

    /**
     * Create a temp group with default settings.
     * @param name Name of virtual group, must match model name
     * @param model Associated model
     * @param plan Appropriate query plan definition object for the temp group
     * @return FakeMetadataObject Metadata object for group
     */
    public static FakeMetadataObject createTempGroup(String name, FakeMetadataObject model, Object plan) {
        FakeMetadataObject obj = new FakeMetadataObject(name, FakeMetadataObject.GROUP);
        obj.putProperty(FakeMetadataObject.Props.MODEL, model);
        obj.putProperty(FakeMetadataObject.Props.IS_VIRTUAL, Boolean.FALSE);
        obj.putProperty(FakeMetadataObject.Props.PLAN, plan);
        obj.putProperty(FakeMetadataObject.Props.UPDATE, Boolean.FALSE); 
        obj.putProperty(FakeMetadataObject.Props.TEMP, Boolean.TRUE);  
        return obj; 
    }
	
    /**
     * Create a virtual group that allows updates with default settings.
     */
	public static Table createUpdatableVirtualGroup(String name, Schema model, QueryNode plan) {
		return createUpdatableVirtualGroup(name, model, plan, null);
	}
    
    public static Table createUpdatableVirtualGroup(String name, Schema model, QueryNode plan, String updatePlan) {
        Table table = createVirtualGroup(name, model, plan);
        table.setUpdatePlan(updatePlan);
        table.setSupportsUpdate(true);
        return table;
    }
		
    public static Column createElement(String name, ColumnSet<?> group, String type) { 
        Column column = new Column();
        column.setName(name);
        group.addColumn(column);
        column.setRuntimeType(type);
        if(type.equals(DataTypeManager.DefaultDataTypes.STRING)) {  
            column.setSearchType(SearchType.Searchable);        
        } else if (DataTypeManager.isNonComparable(type)){
        	column.setSearchType(SearchType.Unsearchable);
        } else {        	
        	column.setSearchType(SearchType.All_Except_Like);
        }   
        column.setNullType(NullType.Nullable);
        column.setPosition(group.getColumns().size()); //1 based indexing
        column.setUpdatable(true);
        column.setLength(100);
        column.setNameInSource(name);
        return column; 
    }
    
    /**
     * Create a set of elements in batch 
     */
	public static List<Column> createElements(ColumnSet<?> group, String[] names, String[] types) { 
		return createElementsWithDefaults(group, names, types, new String[names.length]);
	}

    /**
     * Create a set of elements in batch 
     */
	public static List<Column> createElementsWithDefaults(ColumnSet<?> group, String[] names, String[] types, String[] defaults) {
		List<Column> elements = new ArrayList<Column>();
		
		for(int i=0; i<names.length; i++) { 
            Column element = createElement(names[i], group, types[i]);
            element.setDefaultValue(defaults[i]);
			elements.add(element);		
		}
		
		return elements;
	}	

    /**
     * Create index.  The name will be used as the Object metadataID.
     * @param name String name of index
     * @param group the group for the index
     * @param elements the elements of the index (will be used as if they were
     * metadata IDs)
     * @return key metadata object
     */
    public static FakeMetadataObject createIndex(String name, FakeMetadataObject group, List elements) { 
        FakeMetadataObject obj = new FakeMetadataObject(name, FakeMetadataObject.KEY);
        obj.putProperty(FakeMetadataObject.Props.KEY_TYPE, FakeMetadataObject.TYPE_INDEX);
        obj.putProperty(FakeMetadataObject.Props.KEY_ELEMENTS, elements);
        Collection keys = (Collection)group.getProperty(FakeMetadataObject.Props.KEYS);
        if (keys == null){
            keys = new ArrayList();
            group.putProperty(FakeMetadataObject.Props.KEYS, keys);
        }
        keys.add(obj);
        return obj; 
    }

	/**
	 * Create primary key.  The name will be used as the Object metadataID.
	 * @param name String name of key
	 * @param group the group for the key
	 * @param elements the elements of the key (will be used as if they were
	 * metadata IDs)
	 * @return key metadata object
	 */
	public static FakeMetadataObject createKey(String name, FakeMetadataObject group, List elements) { 
		FakeMetadataObject obj = new FakeMetadataObject(name, FakeMetadataObject.KEY);
        obj.putProperty(FakeMetadataObject.Props.KEY_TYPE, FakeMetadataObject.TYPE_PRIMARY_KEY);
		obj.putProperty(FakeMetadataObject.Props.KEY_ELEMENTS, elements);
		Collection keys = (Collection)group.getProperty(FakeMetadataObject.Props.KEYS);
		if (keys == null){
			keys = new ArrayList();
			group.putProperty(FakeMetadataObject.Props.KEYS, keys);
		}
		keys.add(obj);
		return obj; 
	}

    /**
     * Create foreign key.  The name will be used as the Object metadataID.
     * @param name String name of key
     * @param group the group for the key
     * @param elements the elements of the key (will be used as if they were
     * @param primaryKey referenced by this foreign key
     * metadata IDs)
     * @return key metadata object
     */
    public static FakeMetadataObject createForeignKey(String name, FakeMetadataObject group, List elements, FakeMetadataObject primaryKey) { 
        FakeMetadataObject obj = new FakeMetadataObject(name, FakeMetadataObject.KEY);
        obj.putProperty(FakeMetadataObject.Props.KEY_TYPE, FakeMetadataObject.TYPE_FOREIGN_KEY);
        obj.putProperty(FakeMetadataObject.Props.KEY_ELEMENTS, elements);
        obj.putProperty(FakeMetadataObject.Props.REFERENCED_KEY, primaryKey);
        Collection keys = (Collection)group.getProperty(FakeMetadataObject.Props.KEYS);
        if (keys == null){
            keys = new ArrayList();
            group.putProperty(FakeMetadataObject.Props.KEYS, keys);
        }
        keys.add(obj);
        return obj; 
    }

    /**
     * Create access pattern (currently an access pattern is implemented as a type of key).  The name will
     * be used as the Object metadataID.
     * @param name String name of key
     * @param group the group for the access pattern
     * @param elements the elements of the access pattern (will be used as if they were
     * metadata IDs)
     * @return Access pattern metadata object
     */
    public static FakeMetadataObject createAccessPattern(String name, FakeMetadataObject group, List elements) { 
        FakeMetadataObject obj = new FakeMetadataObject(name, FakeMetadataObject.KEY);
        obj.putProperty(FakeMetadataObject.Props.KEY_TYPE, FakeMetadataObject.TYPE_ACCESS_PATTERN);
        obj.putProperty(FakeMetadataObject.Props.KEY_ELEMENTS, elements);
        Collection keys = (Collection)group.getProperty(FakeMetadataObject.Props.KEYS);
        if (keys == null){
            keys = new ArrayList();
            group.putProperty(FakeMetadataObject.Props.KEYS, keys);
        }
        keys.add(obj);
        return obj; 
    }
    
    /**
     * Create stored procedure parameter.
     */
    public static ProcedureParameter createParameter(String name, int index, int direction, String type, Object rs) {
        ProcedureParameter param = new ProcedureParameter();
        param.setName(name);
        switch (direction) {
        case SPParameter.IN:
        	param.setType(Type.In);
        	break;
        case SPParameter.INOUT:
        	param.setType(Type.InOut);
        	break;
        case SPParameter.OUT:
        	param.setType(Type.Out);
        	break;
        case SPParameter.RESULT_SET:
        	throw new AssertionError("should not directly create a resultset param"); //$NON-NLS-1$
        case SPParameter.RETURN_VALUE:
        	param.setType(Type.ReturnValue);
        	break;
        }
        param.setRuntimeType(type);
        return param;
    }

    /**
     * Create stored procedure.
     * @param name Name of procedure, must match model name
     * @param model Metadata object for the model
     * @param params List of FakeMetadataObject that are the parameters for the procedure
     * @param callableName Callable name of procedure, usually same as procedure name
     * @return Metadata object for stored procedure
     */
    public static Procedure createStoredProcedure(String name, Schema model, List<ProcedureParameter> params, String callableName) {
    	Procedure proc = new Procedure();
    	proc.setName(name);
    	if (params != null) {
    		int index = 1;
	    	for (ProcedureParameter procedureParameter : params) {
				procedureParameter.setProcedure(proc);
				procedureParameter.setPosition(index++);
			}
	    	proc.setParameters(params);
    	}
    	model.addProcedure(proc);
        return proc;    
    }
    
    /**
     * Create virtual sotred procedure.
     * @param name Name of stored query, must match model name
     * @param model Metadata object for the model
     * @param params List of FakeMetadataObject that are the parameters for the procedure
     * @param queryPlan Object representing query plan
     * @return Metadata object for stored procedure
     */
    public static Procedure createVirtualProcedure(String name, Schema model, List<ProcedureParameter> params, QueryNode queryPlan) {
    	Procedure proc = createStoredProcedure(name, model, params, null);
    	proc.setVirtual(true);
    	proc.setQueryPlan(queryPlan.getQuery());
        return proc;
    }
    
    /**
     * Create a result set.
     */
    public static ColumnSet<Procedure> createResultSet(String name, Object model, String[] colNames, String[] colTypes) {
    	ColumnSet<Procedure> rs = new ColumnSet<Procedure>();
    	rs.setName(name);
        for(Column column : createElements(rs, colNames, colTypes)) {
        	column.setParent(rs);
        }
        return rs;
    }

}