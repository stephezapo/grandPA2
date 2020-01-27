package grandpahost;


public class ByteUtils 
{   
    public static int ByteToUint(byte b) 
    {
        return (b < 0 ? 256 + b : b);
    }

    public static String Hex(int value, int digits)
    {
        String string = Integer.toHexString(value).toUpperCase();
        int length = string.length();
        if (length > digits)
            return string.substring(length - digits);
        else if (length < digits)
            return "00000000".substring(8 - (digits - length)) + string;

        return string;
    }

    public static boolean CompareBytes(byte[] data, byte[] other, int offSet, int length) 
    {
        boolean isEqual = (offSet + length) < data.length;
        for (int i = 0; i < length && isEqual; i++)
            isEqual = data[offSet++] == other[i];
  
        return isEqual;
    }

    public static byte[] GetByteChunk(byte[] data, byte[] buffer, int offSet, int len) 
    {
        if (buffer == null)
            buffer = new byte[len];

        System.arraycopy(data, offSet, buffer, 0, len);
        return buffer;
    }

    public static int GetInt16(byte[] data, int index)
    {
        return (GetInt8(data, index) << 8) | GetInt8(data, index + 1);
    }

    public static int GetInt16LE(byte[] data, int index) 
    {
        return (GetInt8(data, index + 1) << 8) | GetInt8(data, index);
    }
    
    public static int GetInt32(byte[] data, int index) 
    {
        return GetInt16(data, index) << 16 | GetInt16(data, index + 2);
    }

    public static int GetInt32LE(byte[] data, int index) 
    {
        return GetInt16LE(data, index + 2) << 16 | GetInt16LE(data, index);
    }

    public static long GetLong64(byte[] data, int index)
    {
        return (long)(GetInt32(data, index)) << 32 | (long)(GetInt16(data, index + 4));
    }

    public static long GetLong64LE(byte[] data, int index)
    {
        return (long)(GetInt32LE(data, index + 4)) << 32 | (long)(GetInt32LE(data, index));
    }

    public static long GetLong48(byte[] data, int index)
    {
        return (long)(GetInt16(data, index)) << 32 | (long)(GetInt16(data, index + 2)) << 16 | (long)(GetInt16(data, index + 4));
    }

    public static long GetLong48LE(byte[] data, int index) 
    {
        return (long)(GetInt16(data, index + 4)) << 32 | (long)(GetInt16(data, index + 2)) << 16 | (long)(GetInt16(data, index));
    }
    
    public static int GetInt8(byte[] data, int index)
    {
        return ByteToUint(data[index]);
    }

    public static void SetByteChunk(byte[] data, byte[] fromBuffer, int offSet, int len)
    {
        System.arraycopy(fromBuffer, 0, data, offSet, len);
    }

    public static void SetLong64(byte[] data, long val, int index)
    {
        SetInt16(data, (int)(val >> 48), index);
        SetInt16(data, (int)(val >> 32), index+2);
        SetInt16(data, (int)(val >> 16), index+4);
        SetInt16(data, (int)(val), index+6);
    }
    
    public static void SetLong48(byte[] data, long val, int index)
    {
        SetInt16(data, (int)(val >> 32), index);
        SetInt16(data, (int)(val >> 16), index+2);
        SetInt16(data, (int)(val), index+4);
    }
    
    public static void SetInt32(byte[] data, int val, int index)
    {
        SetInt16(data, (int)(val >> 16), index);
        SetInt16(data, (int)(val), index+2);
    }
    
    public static void SetInt16(byte[] data, int val, int index) 
    {
        data[index] = (byte) (val >> 8 & 0xff);
        data[index + 1] = (byte) (val & 0xff);
    }

    public static void SetInt16LE(byte[] data, int val, int index)
    {
        data[index] = (byte) (val & 0xff);
        data[index + 1] = (byte) (val >> 8 & 0xff);
    }

    public static void SetInt8(byte[] data, int val, int index)
    {
        data[index] = (byte) (val & 0xff);
    }

    public static String ToHex(byte[] data, int length) 
    {
        String result = "";
        String ascii = "";
        for (int i = 0; i < length; i++) 
        {
            if (0 == i % 16)
            {
                result += Hex(i, 4) + ": ";
                ascii = " ";
            }
            result += Hex(ByteToUint(data[i]), 2);
            ascii += data[i] > 0x1f && data[i] < 0x7f ? (char) data[i] : ".";
            if (7 == i % 8) 
            {
                result += " ";
                ascii += " ";
            }
            result += (15 == i % 16 ? ascii + "\n" : " ");
        }
        
        return result;
    }
}