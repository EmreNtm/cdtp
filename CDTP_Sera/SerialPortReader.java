import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialPortReader implements SerialPortEventListener {

	private int temperatureByteAmount = 4;
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		//System.out.println("type: " + event.getEventType() + ", byte: " + event.getEventValue());
		if(event.isRXCHAR()){
            if(event.getEventValue() == temperatureByteAmount){
                try {
                    byte[] buffer = Main.serialPort.readBytes(temperatureByteAmount);
                    System.out.println("Okunan deger: " + getFloatFromBytes(buffer));
                    Main.serverConnection.updateAndSendSeraInfo(getFloatFromBytes(buffer));
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
        else if(event.isCTS()){//If CTS line has changed state
            if(event.getEventValue() == 1){//If line is ON
                System.out.println("CTS - ON");
            }
            else {
                System.out.println("CTS - OFF");
            }
        }
        else if(event.isDSR()){///If DSR line has changed state
            if(event.getEventValue() == 1){//If line is ON
                System.out.println("DSR - ON");
            }
            else {
                System.out.println("DSR - OFF");
            }
        }
	}
	
	public static boolean writeToSerialPort(String message) {
		if (!Main.isReadyToSendDataToSera())
			return false;
		Main.lastSeraInfoSendTime = System.currentTimeMillis();
		try {
			return Main.serialPort.writeBytes(message.getBytes());
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static float getFloatFromBytes(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}
	
	public static byte[] getBytesFromFloat(float value) {
		return ByteBuffer.allocate(4).putFloat(value).array();
	}
	
}
