package org.biojava.nbio.core.util;


import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UncompressInputStreamProduct extends FilterInputStream {
	protected UncompressInputStreamProduct(InputStream in) {
		super(in);
		// TODO Auto-generated constructor stub
	}

	private final byte[] data = new byte[10000];
	private int end = 0;
	private int got = 0;

	public byte[] getData() {
		return data;
	}

	public int getEnd() {
		return end;
	}

	public int getGot() {
		return got;
	}

	/**
	* @throws IOException
	*/
	public void extraFill() throws IOException {
		if (end < UncompressInputStream.EXTRA)
			fill();
	}

	/**
	* Moves the unread data in the buffer to the beginning and resets the pointers.
	*/
	public int resetbuf(int bit_pos) {
		int pos = bit_pos >> 3;
		System.arraycopy(data, pos, data, 0, end - pos);
		end -= pos;
		return 0;
	}

	public void fill() throws IOException {
		got = in.read(data, end, data.length - 1 - end);
		if (got > 0)
			end += got;
	}

	/**
	 * @param l_tab_prefix
	 * @param l_tab_suffix
	 * @param l_maxmaxcode
	 * @param l_oldcode
	 * @param l_finchar
	 * @param l_free_ent
	 * @return
	 */
	int newEntryTable(int[] l_tab_prefix, byte[] l_tab_suffix, int l_maxmaxcode, int l_oldcode, byte l_finchar, int l_free_ent) {
		if (l_free_ent < l_maxmaxcode) {
			l_tab_prefix[l_free_ent] = l_oldcode;
			l_tab_suffix[l_free_ent] = l_finchar;
			l_free_ent++;
		}
		return l_free_ent;
	}

	void parse_header(UncompressInputStream uncompressInputStream) throws IOException {
		// read in and check magic number
		int t = in.read();
		if (t < 0) throw new EOFException("Failed to read magic number");
		int magic = (t & 0xff) << 8;
		t = in.read();
		if (t < 0) throw new EOFException("Failed to read magic number");
		magic += t & 0xff;
		if (magic != UncompressInputStream.LZW_MAGIC)
			throw new IOException("Input not in compress format (read " +
					"magic number 0x" +
					Integer.toHexString(magic) + ")");
	
		// read in header byte
		int header = in.read();
		if (header < 0) throw new EOFException("Failed to read header");
	
		uncompressInputStream.block_mode = (header & UncompressInputStream.HDR_BLOCK_MODE) > 0;
		uncompressInputStream.maxbits = header & UncompressInputStream.HDR_MAXBITS;
	
		if (uncompressInputStream.maxbits > UncompressInputStream.MAX_BITS)
			throw new IOException("Stream compressed with " + uncompressInputStream.maxbits +
					" bits, but can only handle " + UncompressInputStream.MAX_BITS +
					" bits");
	
		if ((header & UncompressInputStream.HDR_EXTENDED) > 0)
			throw new IOException("Header extension bit set");
	
		if ((header & UncompressInputStream.HDR_FREE) > 0)
			throw new IOException("Header bit 6 set");
	
		UncompressInputStream.logger.debug("block mode: {}", uncompressInputStream.block_mode);
		UncompressInputStream.logger.debug("max bits:   {}", uncompressInputStream.maxbits);
	
		// initialize stuff
		uncompressInputStream.maxmaxcode = 1 << uncompressInputStream.maxbits;
		uncompressInputStream.n_bits = UncompressInputStream.INIT_BITS;
		uncompressInputStream.maxcode = (1 << uncompressInputStream.n_bits) - 1;
		uncompressInputStream.bitmask = uncompressInputStream.maxcode;
		uncompressInputStream.oldcode = -1;
		uncompressInputStream.finchar = 0;
		uncompressInputStream.free_ent = uncompressInputStream.block_mode ? UncompressInputStream.TBL_FIRST : 256;
	
		uncompressInputStream.tab_prefix = new int[1 << uncompressInputStream.maxbits];
		uncompressInputStream.tab_suffix = new byte[1 << uncompressInputStream.maxbits];
		uncompressInputStream.stack = new byte[1 << uncompressInputStream.maxbits];
		uncompressInputStream.stackp = uncompressInputStream.stack.length;
	
		for (int idx = 255; idx >= 0; idx--)
			uncompressInputStream.tab_suffix[idx] = (byte) idx;
	}
}