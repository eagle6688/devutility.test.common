package devutility.test.common.self.nio.usb;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.usb4java.LibUsb;
import org.usb4java.Transfer;
import org.usb4java.TransferCallback;

public class InTransferCallback implements TransferCallback {
	@Override
	public void processTransfer(Transfer transfer) {
		ByteBuffer byteBuffer = transfer.buffer();
		System.out.println(transfer.actualLength() + " bytes received:");
		System.out.println(StandardCharsets.UTF_8.decode(byteBuffer).toString());
		LibUsb.freeTransfer(transfer);
	}
}