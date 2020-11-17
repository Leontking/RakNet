package org.jukeboxmc.raknet.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LucGamesYT
 * @version 1.0
 */
@Getter
@Setter
public class DataPacket extends Packet {

    private List<Object> packets = new ArrayList<>();

    public int sequenceNumber;

    public DataPacket() {
        super( BitFlags.VALID );
    }

    @Override
    public void read() {
        this.getBuffer().readByte();
        this.sequenceNumber = this.getBuffer().readUnsignedMediumLE();

        while ( !this.feof() ) {
            this.packets.add( EncapsulatedPacket.fromBinary( this.getBuffer() ) );
        }
    }

    @Override
    public void write() {
        super.write();
        this.writeLTriad( this.sequenceNumber );

        for ( Object object : this.packets ) {
            if ( object instanceof EncapsulatedPacket ) {
                EncapsulatedPacket encapsulatedPacket = (EncapsulatedPacket) object;
                this.getBuffer().writeBytes( encapsulatedPacket.toBinary() );
            } else if ( object instanceof byte[] ) {
                byte[] buffer = (byte[]) object;
                this.getBuffer().writeBytes( buffer );
            } else {
                System.out.println( "ELSE -> " + object.getClass().getSimpleName());
            }
        }
    }

    public int length() {
        return this.getBuffer().duplicate().asReadOnly().readerIndex( 0 ).readableBytes();
    }

    @Override
    public String toString() {
        return "DataPacket{" +
                "packets=" + packets.size() +
                ", sequenceNumber=" + sequenceNumber +
                ", packetId=" + packetId +
                ", sendTime=" + sendTime +
                '}';
    }
}