package fi.dy.masa.malilib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fi.dy.masa.malilib.MaLiLib;

public class JsonUtils {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@Nullable
	public static JsonObject getNestedObject(JsonObject parent, String key, boolean create) {
		if (!parent.has(key) || !parent.get(key).isJsonObject()) {
			if (!create) {
				return null;
			}

			JsonObject obj = new JsonObject();
			parent.add(key, obj);
			return obj;
		} else {
			return parent.get(key).getAsJsonObject();
		}
	}

	public static boolean hasBoolean(JsonObject obj, String name) {
		JsonElement el = obj.get(name);

		if (el != null && el.isJsonPrimitive()) {
			try {
				el.getAsBoolean();
				return true;
			} catch (Exception ignore) {
			}
		}

		return false;
	}

	public static boolean hasInteger(JsonObject obj, String name) {
		JsonElement el = obj.get(name);

		if (el != null && el.isJsonPrimitive()) {
			try {
				el.getAsInt();
				return true;
			} catch (Exception ignore) {
			}
		}

		return false;
	}

	public static boolean hasLong(JsonObject obj, String name) {
		JsonElement el = obj.get(name);

		if (el != null && el.isJsonPrimitive()) {
			try {
				el.getAsLong();
				return true;
			} catch (Exception ignore) {
			}
		}

		return false;
	}

	public static boolean hasFloat(JsonObject obj, String name) {
		JsonElement el = obj.get(name);

		if (el != null && el.isJsonPrimitive()) {
			try {
				el.getAsFloat();
				return true;
			} catch (Exception ignore) {
			}
		}

		return false;
	}

	public static boolean hasDouble(JsonObject obj, String name) {
		JsonElement el = obj.get(name);

		if (el != null && el.isJsonPrimitive()) {
			try {
				el.getAsDouble();
				return true;
			} catch (Exception ignore) {
			}
		}

		return false;
	}

	public static boolean hasString(JsonObject obj, String name) {
		JsonElement el = obj.get(name);

		if (el != null && el.isJsonPrimitive()) {
			try {
				el.getAsString();
				return true;
			} catch (Exception ignore) {
			}
		}

		return false;
	}

	public static boolean hasObject(JsonObject obj, String name) {
		JsonElement el = obj.get(name);
		return el != null && el.isJsonObject();
	}

	public static boolean hasArray(JsonObject obj, String name) {
		JsonElement el = obj.get(name);
		return el != null && el.isJsonArray();
	}

	public static boolean getBooleanOrDefault(JsonObject obj, String name, boolean defaultValue) {
		if (obj.has(name) && obj.get(name).isJsonPrimitive()) {
			try {
				return obj.get(name).getAsBoolean();
			} catch (Exception ignore) {
			}
		}

		return defaultValue;
	}

	public static int getIntegerOrDefault(JsonObject obj, String name, int defaultValue) {
		if (obj.has(name) && obj.get(name).isJsonPrimitive()) {
			try {
				return obj.get(name).getAsInt();
			} catch (Exception ignore) {
			}
		}

		return defaultValue;
	}

	public static long getLongOrDefault(JsonObject obj, String name, long defaultValue) {
		if (obj.has(name) && obj.get(name).isJsonPrimitive()) {
			try {
				return obj.get(name).getAsLong();
			} catch (Exception ignore) {
			}
		}

		return defaultValue;
	}

	public static float getFloatOrDefault(JsonObject obj, String name, float defaultValue) {
		if (obj.has(name) && obj.get(name).isJsonPrimitive()) {
			try {
				return obj.get(name).getAsFloat();
			} catch (Exception ignore) {
			}
		}

		return defaultValue;
	}

	public static double getDoubleOrDefault(JsonObject obj, String name, double defaultValue) {
		if (obj.has(name) && obj.get(name).isJsonPrimitive()) {
			try {
				return obj.get(name).getAsDouble();
			} catch (Exception ignore) {
			}
		}

		return defaultValue;
	}

	public static String getStringOrDefault(JsonObject obj, String name, String defaultValue) {
		if (obj.has(name) && obj.get(name).isJsonPrimitive()) {
			try {
				return obj.get(name).getAsString();
			} catch (Exception ignore) {
			}
		}

		return defaultValue;
	}

	public static boolean getBoolean(JsonObject obj, String name) {
		return getBooleanOrDefault(obj, name, false);
	}

	public static int getInteger(JsonObject obj, String name) {
		return getIntegerOrDefault(obj, name, 0);
	}

	public static long getLong(JsonObject obj, String name) {
		return getLongOrDefault(obj, name, 0);
	}

	public static float getFloat(JsonObject obj, String name) {
		return getFloatOrDefault(obj, name, 0);
	}

	public static double getDouble(JsonObject obj, String name) {
		return getDoubleOrDefault(obj, name, 0);
	}

	@Nullable
	public static String getString(JsonObject obj, String name) {
		return getStringOrDefault(obj, name, null);
	}

	public static boolean hasBlockPos(JsonObject obj, String name) {
		return blockPosFromJson(obj, name) != null;
	}

	public static JsonArray blockPosToJson(BlockPos pos) {
		JsonArray arr = new JsonArray();

		arr.add(pos.getX());
		arr.add(pos.getY());
		arr.add(pos.getZ());

		return arr;
	}

	@Nullable
	public static BlockPos blockPosFromJson(JsonObject obj, String name) {
		if (hasArray(obj, name)) {
			JsonArray arr = obj.getAsJsonArray(name);

			if (arr.size() == 3) {
				try {
					return new BlockPos(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
				} catch (Exception ignore) {
				}
			}
		}

		return null;
	}

	public static boolean hasVec3d(JsonObject obj, String name) {
		return vec3dFromJson(obj, name) != null;
	}

	public static JsonArray vec3dToJson(Vec3 vec) {
		JsonArray arr = new JsonArray();

		arr.add(vec.x);
		arr.add(vec.y);
		arr.add(vec.z);

		return arr;
	}

	@Nullable
	public static Vec3 vec3dFromJson(JsonObject obj, String name) {
		if (hasArray(obj, name)) {
			JsonArray arr = obj.getAsJsonArray(name);

			if (arr.size() == 3) {
				try {
					return new Vec3(arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), arr.get(2).getAsDouble());
				} catch (Exception ignore) {
				}
			}
		}

		return null;
	}

	// https://stackoverflow.com/questions/29786197/gson-jsonobject-copy-value-affected-others-jsonobject-instance
	@Nonnull
	public static JsonObject deepCopy(@Nonnull JsonObject jsonObject) {
		JsonObject result = new JsonObject();

		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			result.add(entry.getKey(), deepCopy(entry.getValue()));
		}

		return result;
	}

	@Nonnull
	public static JsonArray deepCopy(@Nonnull JsonArray jsonArray) {
		JsonArray result = new JsonArray();

		for (JsonElement e : jsonArray) {
			result.add(deepCopy(e));
		}

		return result;
	}

	@Nonnull
	public static JsonElement deepCopy(@Nonnull JsonElement jsonElement) {
		if (jsonElement.isJsonPrimitive() || jsonElement.isJsonNull()) {
			return jsonElement; // these are immutable anyway
		} else if (jsonElement.isJsonObject()) {
			return deepCopy(jsonElement.getAsJsonObject());
		} else if (jsonElement.isJsonArray()) {
			return deepCopy(jsonElement.getAsJsonArray());
		} else {
			throw new UnsupportedOperationException("Unsupported element: " + jsonElement);
		}
	}

	@Nullable
	public static JsonElement parseJsonFile(File file) {
		if (file != null && file.exists() && file.isFile() && file.canRead()) {
			String fileName = file.getAbsolutePath();

			try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
				return JsonParser.parseReader(reader);
			} catch (Exception e) {
				MaLiLib.logger.error("Failed to parse the JSON file '{}'", fileName, e);
			}
		}

		return null;
	}

	public static boolean writeJsonToFile(JsonObject root, File file) {
		File fileTmp = new File(file.getParentFile(), file.getName() + ".tmp");

		if (fileTmp.exists()) {
			fileTmp = new File(file.getParentFile(), UUID.randomUUID() + ".tmp");
		}

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileTmp), StandardCharsets.UTF_8)) {
			writer.write(GSON.toJson(root));
			writer.close();

			if (file.exists() && file.isFile() && !file.delete()) {
				MaLiLib.logger.warn("Failed to delete file '{}'", file.getAbsolutePath());
			}

			return fileTmp.renameTo(file);
		} catch (Exception e) {
			MaLiLib.logger.warn("Failed to write JSON data to file '{}'", fileTmp.getAbsolutePath(), e);
		}

		return false;
	}
}
