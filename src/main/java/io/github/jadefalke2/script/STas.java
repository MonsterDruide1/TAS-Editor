package io.github.jadefalke2.script;

import io.github.jadefalke2.InputLine;
import io.github.jadefalke2.Script;
import io.github.jadefalke2.stickRelatedClasses.StickPosition;
import io.github.jadefalke2.util.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class STas {

	public static final short VERSION_FILE_FORMAT = 0x0000;
	public static final short VERSION_EDITOR_EXTRAS = 0x0000;
	public static final long SMBW_TITLE_ID = 0x010015100b514000L;

	public static abstract class Command {
		public byte[] getBytes() {
			ArrayList<byte[]> data = new ArrayList<>();
			data.add(ByteConversions.fromShort(getCommandId(), ByteOrder.LITTLE_ENDIAN));
			byte[] payload = getPayload();
			data.add(ByteConversions.fromShort((short) payload.length, ByteOrder.LITTLE_ENDIAN));
			data.add(payload);
			return Util.merge(data);
		}
		public abstract short getCommandId();
		public abstract byte[] getPayload();
	}

	public static class FrameCommand extends Command {
		private final int frameId;
		public FrameCommand(int frameId) {
			this.frameId = frameId;
		}
		public short getCommandId() {
			return 0x0000;
		}
		public byte[] getPayload() {
			return ByteConversions.fromInt(frameId, ByteOrder.LITTLE_ENDIAN);
		}
	}

	public static class ControllerCommand extends Command {
		private final byte playerId;
		public final EnumSet<Button> buttons;
		private final StickPosition stickL, stickR;
		public ControllerCommand(byte playerId, EnumSet<Button> buttons, StickPosition stickL, StickPosition stickR) {
			this.playerId = playerId;
			this.buttons = buttons;
			this.stickL = stickL;
			this.stickR = stickR;
		}
		public ControllerCommand(byte playerId, InputLine line) {
			this(playerId, line.buttons, line.getStickL(), line.getStickR());
		}
		public short getCommandId() {
			return 0x0001;
		}
		public byte[] getPayload() {
			ArrayList<byte[]> data = new ArrayList<>();
			data.add(new byte[] {playerId});
			data.add(new byte[] {0, 0, 0}); // padding
			data.add(ByteConversions.fromLong(getButtonMask(), ByteOrder.LITTLE_ENDIAN));
			data.add(writeStick(stickL));
			data.add(writeStick(stickR));
			return Util.merge(data);
		}
		private long getButtonMask() {
			long mask = 0;
			for (Button button : buttons) {
				if(button == Button.KEY_A) mask |= (1 << 0);
				else if(button == Button.KEY_B) mask |= (1 << 1);
				else if(button == Button.KEY_X) mask |= (1 << 2);
				else if(button == Button.KEY_Y) mask |= (1 << 3);
				else if(button == Button.KEY_LSTICK) mask |= (1 << 4);
				else if(button == Button.KEY_RSTICK) mask |= (1 << 5);
				else if(button == Button.KEY_L) mask |= (1 << 6);
				else if(button == Button.KEY_R) mask |= (1 << 7);
				else if(button == Button.KEY_ZL) mask |= (1 << 8);
				else if(button == Button.KEY_ZR) mask |= (1 << 9);
				else if(button == Button.KEY_PLUS) mask |= (1 << 10);
				else if(button == Button.KEY_MINUS) mask |= (1 << 11);
				else if(button == Button.KEY_DLEFT) mask |= (1 << 12);
				else if(button == Button.KEY_DUP) mask |= (1 << 13);
				else if(button == Button.KEY_DRIGHT) mask |= (1 << 14);
				else if(button == Button.KEY_DDOWN) mask |= (1 << 15);
				else throw new RuntimeException("Unknown button: " + button);
			}

			if(stickL.getX() < -0.5f)
				mask |= (1 << 16);
			if(stickL.getY() > 0.5f)
				mask |= (1 << 17);
			if(stickL.getX() > 0.5f)
				mask |= (1 << 18);
			if(stickL.getY() < -0.5f)
				mask |= (1 << 19);

			if(stickR.getX() < -0.5f)
				mask |= (1 << 20);
			if(stickR.getY() > 0.5f)
				mask |= (1 << 21);
			if(stickR.getX() > 0.5f)
				mask |= (1 << 22);
			if(stickR.getY() < -0.5f)
				mask |= (1 << 23);

			//FIXME others? SL/SR missing
			return mask;
		}
		public static byte[] writeStick(StickPosition stick) {
			return Util.merge(ByteConversions.fromInt(stick.getX(), ByteOrder.LITTLE_ENDIAN), ByteConversions.fromInt(stick.getY(), ByteOrder.LITTLE_ENDIAN));
		}
		public static StickPosition readStick(ByteDataStream data) {
			return new StickPosition(data.getInt(), data.getInt());
		}
	}

	public static void write(Script script, File file) throws IOException {
		Logger.log("saving script to " + file.getAbsolutePath());
		Util.writeFile(write(script), file);
	}

	public static byte[] write(Script script) {
		InputLine[] inputLines = script.getLines();

		ArrayList<Command> commands = new ArrayList<>();
		int lastNonEmptyLine = -1;
		for(int i=0; i<inputLines.length; i++) {
			if(!inputLines[i].isEmpty()) lastNonEmptyLine = i;
			if(i>0 && inputLines[i].equals(inputLines[i-1])) continue;
			commands.add(new FrameCommand(i));
			commands.add(new ControllerCommand((byte) 0, inputLines[i]));
		}
		if(!inputLines[inputLines.length-1].isEmpty()) {
			commands.add(new FrameCommand(lastNonEmptyLine+1));
			commands.add(new ControllerCommand((byte) 0, InputLine.getEmpty()));
		}

		ArrayList<byte[]> data = new ArrayList<>();

		data.add(new byte[] {/* STAS */ 0x53, 0x54, 0x41, 0x53}); // magic
		data.add(ByteConversions.fromShort(VERSION_FILE_FORMAT, ByteOrder.LITTLE_ENDIAN)); // version - file format
		data.add(ByteConversions.fromShort((short) 0x0001, ByteOrder.LITTLE_ENDIAN)); // version - game addon
		data.add(ByteConversions.fromShort(VERSION_EDITOR_EXTRAS, ByteOrder.LITTLE_ENDIAN)); // version - editor extras
		data.add(new byte[] {0, 0}); // padding
		data.add(ByteConversions.fromLong(SMBW_TITLE_ID, ByteOrder.LITTLE_ENDIAN)); // hardcoded to SMBW

		data.add(ByteConversions.fromInt(commands.size(), ByteOrder.LITTLE_ENDIAN)); // command count
		data.add(ByteConversions.fromInt(script.getEditingSeconds(), ByteOrder.LITTLE_ENDIAN)); // how long the script has been open/edited for
		data.add(new byte[] {1}); // player count - currently ignored
		data.add(new byte[] {0, 0, 0}); // padding
		data.add(new byte[] {0}); // controller type of player 0: procon
		data.add(new byte[] {0, 0, 0}); // padding

		byte[] authorAnyLen = Settings.INSTANCE.authorName.get().getBytes(StandardCharsets.UTF_8);
		byte[] author = Arrays.copyOf(authorAnyLen, Math.min(authorAnyLen.length, (int)Math.pow(2, 16)-1));
		data.add(ByteConversions.fromShort((short) author.length, ByteOrder.LITTLE_ENDIAN)); // author name length
		data.add(author); // author name
		data.add(new byte[(4-((author.length+4+2)%4))%4]); //align

		for(Command c : commands) {
			data.add(c.getBytes());
		}

		byte[] full = Util.merge(data);
		try {
			read(full);
		} catch(Exception e) {
			// not thrown, as just creating it is enough for the box to show up
			new CorruptedScriptException("Verification of the saved script failed! You won't be able to open the saved .stas file again. Please make a backup in .txt form instead, and report this issue to MonsterDruide1!", -1, e);
		}

		return Util.merge(data);
	}

	public static Script read(File file) throws CorruptedScriptException, IOException {
		Script s = read(Util.fileToBytes(file));
		s.setFile(file, Format.STAS);
		return s;
	}

	public static Script read(byte[] script) throws CorruptedScriptException {
		ByteDataStream data = new ByteDataStream(script, ByteOrder.LITTLE_ENDIAN);

		data.assertMagic("STAS");
		data.expectShort(VERSION_FILE_FORMAT, "version of file format");
		data.expectShort(0x0001, "version of game addon");
		data.expectShort(VERSION_EDITOR_EXTRAS, "version of editor extras");
		data.align(4);
		data.expectLong(SMBW_TITLE_ID, "game title id"); // hardcoded to SMBW

		int commandCount = data.getInt();
		int editingSeconds = data.getInt();
		data.storePos();
		System.out.println(Arrays.toString(data.getBytes(0x20)));
		data.loadPos();
		data.expectByte(1, "player count"); // currently ignored
		data.align(4);
		data.expectByte(0, "controller type of player 0"); // procon
		data.align(4);

		short authorLength = data.getShort();
		String author = data.getString(authorLength, StandardCharsets.UTF_8);
		// ignored, as it will be overridden by own name on export
		data.align(4);

		List<InputLine> inputLines = new ArrayList<>();

		for(int i=0; i<commandCount; i++) {
			Command c = readCommand(data);
			if(c instanceof FrameCommand) {
				FrameCommand fc = (FrameCommand) c;
				if(fc.frameId < inputLines.size())
					throw new CorruptedScriptException("Line numbers misordered, got "+fc.frameId+" after "+inputLines.size()+", offset "+Integer.toHexString(data.position()), inputLines.size());
				while(inputLines.size() <= fc.frameId) {
					if(inputLines.isEmpty())
						inputLines.add(InputLine.getEmpty());
					else
						inputLines.add(inputLines.get(inputLines.size()-1).clone());
				}
			} else if(c instanceof ControllerCommand) {
				ControllerCommand cc = (ControllerCommand) c;
				inputLines.set(inputLines.size()-1, new InputLine(cc.buttons, cc.stickL, cc.stickR));
			} else {
				throw new RuntimeException("Unknown command: " + c);
			}
		}
		data.assertEOF();
		return new Script(inputLines.toArray(new InputLine[0]), editingSeconds);
	}

	public static Command readCommand(ByteDataStream data) {
		data.storePos();
		data.loadPos();
		short commandId = data.getShort();
		short payloadLength = data.getShort();
		int position = data.position();

		Command c;
		if(commandId == 0x0000) {
			c = new FrameCommand(data.getInt());
		} else if(commandId == 0x0001) {
			byte playerId = data.getByte();
			data.align(4);
			long button = data.getLong();
			StickPosition stickL = ControllerCommand.readStick(data);
			StickPosition stickR = ControllerCommand.readStick(data);

			EnumSet<Button> buttons = EnumSet.noneOf(Button.class);
			if ((button & (1 << 0)) != 0) buttons.add(Button.KEY_A);
			if ((button & (1 << 1)) != 0) buttons.add(Button.KEY_B);
			if ((button & (1 << 2)) != 0) buttons.add(Button.KEY_X);
			if ((button & (1 << 3)) != 0) buttons.add(Button.KEY_Y);
			if ((button & (1 << 4)) != 0) buttons.add(Button.KEY_LSTICK);
			if ((button & (1 << 5)) != 0) buttons.add(Button.KEY_RSTICK);
			if ((button & (1 << 6)) != 0) buttons.add(Button.KEY_L);
			if ((button & (1 << 7)) != 0) buttons.add(Button.KEY_R);
			if ((button & (1 << 8)) != 0) buttons.add(Button.KEY_ZL);
			if ((button & (1 << 9)) != 0) buttons.add(Button.KEY_ZR);
			if ((button & (1 << 10)) != 0) buttons.add(Button.KEY_PLUS);
			if ((button & (1 << 11)) != 0) buttons.add(Button.KEY_MINUS);
			if ((button & (1 << 12)) != 0) buttons.add(Button.KEY_DLEFT);
			if ((button & (1 << 13)) != 0) buttons.add(Button.KEY_DUP);
			if ((button & (1 << 14)) != 0) buttons.add(Button.KEY_DRIGHT);
			if ((button & (1 << 15)) != 0) buttons.add(Button.KEY_DDOWN);

			c = new ControllerCommand(playerId, buttons, stickL, stickR);
		} else {
			throw new RuntimeException("Unknown command: " + commandId+" at offset "+Integer.toHexString(data.position()));
		}
		data.assertPosition(position+payloadLength);
		return c;
	}

}
