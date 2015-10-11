import java.io.{PrintWriter, BufferedOutputStream, BufferedInputStream}
import java.net.{Socket, InetAddress}
import java.nio.charset.StandardCharsets
import java.nio.{ByteOrder, ByteBuffer}

object Main
{

	object GCM
	{
		val in = new BufferedInputStream(System.in)
		val out = new BufferedOutputStream(System.out)
		val err = new PrintWriter(System.err)

		private val native = ByteOrder.nativeOrder()

		def receive(): String =
		{
			val length = {
				val bytes = Array.ofDim[Byte](4)
				in.read(bytes, 0, bytes.length)
				/* For messages longer than 2^32-1 byte, this will crash:
				 * The size is an unsigned int:
				 * https://developer.chrome.com/extensions/nativeMessaging#native-messaging-host-protocol
				 */
				// not a signed int from -2^31 to 2^31-1.
				ByteBuffer.wrap(bytes).order(native).getInt
			}

			val bytes = Array.ofDim[Byte](length)
			in.read(bytes, 0, bytes.length)
			new String(bytes, 0, bytes.length, StandardCharsets.UTF_8)
		}

		def send(message: String): Unit =
		{
			out.write(ByteBuffer.allocate(4).order(native).putInt(message.length).array(), 0, 4)
			val bytes = StandardCharsets.UTF_8.encode(message).array()
			out.write(bytes, 0, bytes.length)
			out.flush()
		}
	}

	implicit def toRunnable(f: () => Unit): Runnable = new Runnable()
	{override def run() = f()}

	def main(args: Array[String]): Unit =
	{
		val nativePort: Option[Int] = {
			val extract = """"port"\s*:\s*(\d+)""".r.unanchored
			GCM.receive() match {
				case extract(port) => Some(port.toInt)
				case _ => None
			}
		}

		if (nativePort.isEmpty) {
			GCM.err.println("Did not receive native port")
			GCM.err.flush()
			return 1
		}

		GCM.err.println("[GCMN] Received native port: %d".format(nativePort))
		GCM.err.flush()

		val (nativeIn, nativeOut) = {
			val nativeSocket = new Socket(InetAddress.getByName("localhost"): InetAddress, nativePort.get)
			(nativeSocket.getInputStream, nativeSocket.getOutputStream)
		}

		new Thread(() => {
			val buffer = Array.ofDim[Byte](1024)
			var length: Int = 0
			while ( {length = GCM.in.read(buffer); length} != -1)
				nativeOut.write(buffer, 0, length)
		}).start()

		new Thread(() => {
			val buffer = Array.ofDim[Byte](1024)
			var length: Int = 0
			while ( {length = nativeIn.read(buffer); length} != -1)
				GCM.out.write(buffer, 0, length)
		}).start()
	}
}
