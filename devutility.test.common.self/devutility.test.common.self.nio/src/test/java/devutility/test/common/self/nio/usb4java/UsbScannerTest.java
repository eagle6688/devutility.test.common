package devutility.test.common.self.nio.usb4java;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.usb4java.BufferUtils;
import org.usb4java.Context;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;
import org.usb4java.Transfer;

import devutility.internal.test.TestExecutor;
import devutility.test.common.self.nio.BaseTest;
import devutility.test.common.self.nio.usb.AsyncEventHandlingThread;
import devutility.test.common.self.nio.usb.InTransferCallback;

public class UsbScannerTest extends BaseTest {
	private static final short VENDOR_ID = 0x2010;
	private static final short PRODUCT_ID = 0x7638;
	private static final byte INTERFACE_NUMBER = 0;
	private static final byte ENDPOINT_ADDRESS = (byte) 0x83;

	@Override
	public void run() {
		Context context = new Context();
		int result = LibUsb.init(context);

		if (result < 0) {
			throw new LibUsbException("Unable to initialize libusb", result);
		}

		try {
			DeviceHandle handle = LibUsb.openDeviceWithVidPid(context, VENDOR_ID, PRODUCT_ID);

			if (handle == null) {
				System.err.println("Test device not found.");
			}

			AsyncEventHandlingThread thread = new AsyncEventHandlingThread();
			thread.setContext(context);
			thread.start();

			result = LibUsb.claimInterface(handle, INTERFACE_NUMBER);

			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException("Unable to claim interface", result);
			}

			InTransferCallback callback = new InTransferCallback();
			ByteBuffer buffer = BufferUtils.allocateByteBuffer(8).order(ByteOrder.LITTLE_ENDIAN);
			Transfer transfer = LibUsb.allocTransfer(0);
			LibUsb.fillInterruptTransfer(transfer, handle, ENDPOINT_ADDRESS, buffer, callback, null, 5000);
			result = LibUsb.submitTransfer(transfer);

			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException("Unable to submit transfer", result);
			}

			result = LibUsb.releaseInterface(handle, INTERFACE_NUMBER);

			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException("Unable to release interface", result);
			}

			LibUsb.close(handle);

			thread.abort();

			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			LibUsb.exit(context);
			System.out.println("Program finished");
		} finally {
			LibUsb.exit(context);
		}
	}

	public static void main(String[] args) {
		TestExecutor.run(UsbScannerTest.class);
	}
}