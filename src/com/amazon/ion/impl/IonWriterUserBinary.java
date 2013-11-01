// Copyright (c) 2010-2013 Amazon.com, Inc.  All rights reserved.

package com.amazon.ion.impl;

import static com.amazon.ion.impl._Private_Utils.isNonSymbolScalar;
import static com.amazon.ion.impl._Private_Utils.symtabExtends;

import com.amazon.ion.IonCatalog;
import com.amazon.ion.IonReader;
import com.amazon.ion.IonType;
import com.amazon.ion.SymbolTable;
import com.amazon.ion.ValueFactory;
import com.amazon.ion.util.IonStreamUtils;
import java.io.IOException;

/**
 *
 */
class IonWriterUserBinary
    extends IonWriterUser
    implements _Private_ListWriter
{
    public final boolean myStreamCopyOptimized;

    // If we wanted to we could keep an extra reference to the
    // system writer which was correctly typed as an
    // IonBinaryWriter and avoid the casting in the 3 "overridden"
    // methods.  However those are sufficiently expensive that
    // the cost of the cast should be lost in the noise.

    IonWriterUserBinary(IonCatalog catalog,
                        ValueFactory symtabValueFactory,
                        IonWriterSystemBinary systemWriter,
                        boolean streamCopyOptimized,
                        SymbolTable... imports)
    {
        super(catalog, symtabValueFactory, systemWriter, imports);
        myStreamCopyOptimized = streamCopyOptimized;
    }


    @Override
    public boolean isStreamCopyOptimized()
    {
        return myStreamCopyOptimized;
    }


    @Override
    public void writeValue(IonReader reader)
        throws IOException
    {
        // If reader is not on a value, type is null, and NPE will be thrown
        // by calls below
        IonType type = reader.getType();

        // See if we can copy bytes directly from the source. This test should
        // only happen at the outermost call, not recursively down the tree.

        ByteTransferReader transfer = reader.asFacet(ByteTransferReader.class);

        if (myStreamCopyOptimized
            && transfer != null
            && _current_writer instanceof IonWriterSystemBinary
            && (isNonSymbolScalar(type) ||
                symtabExtends(getSymbolTable(), reader.getSymbolTable())))
        {
            IonWriterSystemBinary systemOut =
                (IonWriterSystemBinary) _current_writer;

            // TODO ION-241 Doesn't copy annotations or field names.
            transfer.transferCurrentValue(systemOut);

            return;
        }

        // From here on, we won't call back into this method, so we won't
        // bother doing all those checks again.
        writeValueRecursively(type, reader);
    }


    public void writeBoolList(boolean[] values) throws IOException
    {
        IonStreamUtils.writeBoolList(_current_writer, values);
    }


    public void writeFloatList(float[] values) throws IOException
    {
        IonStreamUtils.writeFloatList(_current_writer, values);
    }


    public void writeFloatList(double[] values) throws IOException
    {
        IonStreamUtils.writeFloatList(_current_writer, values);
    }


    public void writeIntList(byte[] values) throws IOException
    {
        IonStreamUtils.writeIntList(_current_writer, values);
    }


    public void writeIntList(short[] values) throws IOException
    {
        IonStreamUtils.writeIntList(_current_writer, values);
    }


    public void writeIntList(int[] values) throws IOException
    {
        IonStreamUtils.writeIntList(_current_writer, values);
    }


    public void writeIntList(long[] values) throws IOException
    {
        IonStreamUtils.writeIntList(_current_writer, values);
    }


    public void writeStringList(String[] values) throws IOException
    {
        IonStreamUtils.writeStringList(_current_writer, values);
    }
}
