package grandpahost;


import java.util.Arrays;


public class ArtDmxPacket extends ArtNetPacket 
{
    private int channelCount;
    private int sequenceID;
    private int subnetID;
    private int universeID;
    private byte[] dmxData;

    public ArtDmxPacket() 
    {
        super(PacketType.ART_OUTPUT);
        super.setData(new byte[530]);
        super.setHeader();
        super.setProtocol();
        ByteUtils.SetInt8(data, 0x02, 13);
    }

    public byte[] getDmxData()
    {
        return dmxData;
    }

    @Override
    public int getLength() 
    {
        return 18 + (1 == channelCount % 2 ? channelCount + 1 : channelCount);
    }

    public int getChannelCount() 
    {
        return channelCount;
    }

    public int getSequenceID()
    {
        return sequenceID;
    }

    public int getSubnetID() 
    {
        return subnetID;
    }

    public int getUniverseID() 
    {
        return universeID;
    }

    @Override
    public boolean parse(byte[] raw) 
    {
        setData(raw);
        sequenceID = ByteUtils.GetInt8(data, 12);
        int subnetUniverse = ByteUtils.GetInt8(data, 14);
        subnetID = subnetUniverse >> 4;
        universeID = subnetUniverse & 0x0f;
        channelCount = ByteUtils.GetInt16(data, 16);
        dmxData = ByteUtils.GetByteChunk(data, dmxData, 18, channelCount);
        return true;
    }

    public void setDMX(byte[] dmxData, int channelCount) 
    {
        this.dmxData = Arrays.copyOf(dmxData, channelCount);
        this.channelCount = channelCount;
        ByteUtils.SetByteChunk(data, dmxData, 18, channelCount);
        ByteUtils.SetInt16(data, (1 == channelCount % 2 ? channelCount + 1 : channelCount), 16);
    }

    public void setNumChannels(int numChannels)
    {
        this.channelCount = numChannels > 512 ? 512 : numChannels;
    }

    public void setSequenceID(int id)
    {
        sequenceID = id % 0xff;
        ByteUtils.SetInt8(data, id, 12);
    }

    public void setSubnetID(int subnetID) 
    {
        this.subnetID = subnetID & 0x0f;
    }

    public void setUniverse(int subnetID, int universeID)
    {
        this.subnetID = subnetID & 0x0f;
        this.universeID = universeID & 0x0f;
        ByteUtils.SetInt16LE(data, subnetID << 4 | universeID, 14);
    }

    public void setUniverseID(int universeID)
    {
            this.universeID = universeID & 0x0f;
    }
}