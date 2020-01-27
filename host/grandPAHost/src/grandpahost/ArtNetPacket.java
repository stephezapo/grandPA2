package grandpahost;


import java.net.InetSocketAddress;
import java.util.Arrays;



public abstract class ArtNetPacket 
{
    public static final byte[] HEADER = "Art-Net\0".getBytes();
    public static final int VERSION = 14;
    protected byte[] data;
    protected final PacketType type;
    private InetSocketAddress remoteSocketAdress;

    
    public ArtNetPacket(PacketType type) 
    {
        this.type = type;
    }

    public byte[] getData() 
    {
        return data;
    }

    public int getLength() 
    {
        return data.length;
    }

    public PacketType getType() 
    {
        return type;
    }

    public abstract boolean parse(byte[] raw);

    public void setData(byte[] data)
    {
        this.data = Arrays.copyOf(data, data.length);
    }

    public void setData(byte[] raw, int maxLength)
    {
        if (raw.length > maxLength)
        {
            byte[] raw2 = new byte[maxLength];
            System.arraycopy(raw, 0, raw2, 0, maxLength);
            raw = raw2;
        }
        setData(raw);
    }

    protected void setHeader() 
    {
        ByteUtils.SetByteChunk(data, HEADER, 0, 8);
        ByteUtils.SetInt16LE(data, type.getOpCode(), 8);
    }

    protected void setProtocol() 
    {
        ByteUtils.SetInt16(data, VERSION, 10);
    }

    @Override
    public String toString() 
    {
        return ByteUtils.ToHex(data, getLength());
    }

    public InetSocketAddress getRemoteSocketAdress() 
    {
        return remoteSocketAdress;
    }

    public void setRemoteSocketAdress(InetSocketAddress remoteSocketAdress) 
    {
        this.remoteSocketAdress = remoteSocketAdress;
    }
}