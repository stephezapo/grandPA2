package grandpahost;

public class Constants
{
    public enum HardwareKey{
        CH_PG_PLUS(3),
        CH_PG_MINUS(4),
        FD_PG_PLUS(5),
        FD_PG_MINUS(6),
        BT_PG_PLUS(7),
        BT_PG_MINUS(8),
        LARGE_PAUSE(9),
        LARGE_GO_MINUS(10),
        LARGE_GO_PLUS(11),
        ESC(54),
        SETUP(118);

        private final int value;

        HardwareKey(final int newValue)
        {
            value = newValue;
        }

        public int getValue()
        {
            return value;
        }
    }
}