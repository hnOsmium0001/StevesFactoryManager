package vswe.stevesfactory.ui.manager.selection;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.api.StevesFactoryManagerAPI;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.logic.Procedures;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class ComponentGroup {

    public static final Set<IProcedureType<?>> groupedTypes = new HashSet<>();
    public static final Set<IProcedureType<?>> ungroupedTypes = new HashSet<>();
    public static final List<ComponentGroup> groups = new ArrayList<>();

    private static final String defaultComponentsPath = "/assets/" + StevesFactoryManager.MODID + "/component_groups/";

    private static File getConfigDirectory() {
        return new File("./config/" + StevesFactoryManager.MODID + "/component_groups/");
    }

    public static void reload() {
        reload(false);
    }

    public static void reload(boolean reset) {
        preSetup();

        File directory = getConfigDirectory();
        JsonParser parser = new JsonParser();
        if (reset) {
            try {
                FileUtils.deleteDirectory(directory);
                copySettings(parser, directory);
            } catch (IOException e) {
                StevesFactoryManager.logger.error("Error resetting component group configs", e);
            }
        } else {
            String[] list = directory.list();
            if (!directory.exists() || list == null || list.length == 0) {
                copySettings(parser, directory);
            }
        }

        try {
            setupInternal(parser, directory);
        } catch (IOException e) {
            StevesFactoryManager.logger.error("Error setting up groups", e);
        }

        categorizeTypes();
    }

    private static void preSetup() {
        groups.clear();
        groupedTypes.clear();
        ungroupedTypes.clear();
    }

    private static void copySettings(JsonParser parser, File configDir) {
        boolean success = configDir.mkdirs();

        String ordersFileName = "@orders.json"; // Default value
        try (InputStream loaderIn = StevesFactoryManager.class.getResourceAsStream(defaultComponentsPath + "@loader.json")) {
            // No need to close this because it is essentially a wrapper around the InputStream
            InputStreamReader loadReader = new InputStreamReader(loaderIn);
            JsonObject loaderRoot = parser.parse(loadReader).getAsJsonObject();

            // Parsing files
            JsonArray files = loaderRoot.getAsJsonArray("files");
            for (JsonElement element : files) {
                String fileName = element.getAsString();
                String filePath = defaultComponentsPath + fileName;
                Path configPath = new File(configDir.getPath() + "/" + fileName).toPath();

                // Copy the definition file to config directory
                try (InputStream fileIn = StevesFactoryManager.class.getResourceAsStream(filePath)) {
                    Files.copy(fileIn, configPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    StevesFactoryManager.logger.error("Error copying default component group config file {}", filePath, e);
                }
            }

            JsonElement ordersElement = loaderRoot.get("orders");
            if (ordersElement != null) {
                ordersFileName = ordersElement.getAsString();
            }
        } catch (IOException e) {
            StevesFactoryManager.logger.error("Error reading loader config", e);
        }

        // Copying @orders.json
        try (InputStream orderIn = StevesFactoryManager.class.getResourceAsStream(defaultComponentsPath + ordersFileName)) {
            Files.copy(orderIn, new File(configDir.getPath() + "/@orders.json").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            StevesFactoryManager.logger.error("Error copying default component group order config", e);
        }
    }

    private static void setupInternal(JsonParser parser, File directory) throws IOException {
        Object2IntMap<String> orders = new Object2IntOpenHashMap<>();
        File orderFile = new File(directory, "@order.json");
        try (FileReader reader = new FileReader(orderFile)) {
            JsonObject root = parser.parse(reader).getAsJsonObject();
            JsonArray entries = root.getAsJsonArray("order");
            int i = 1;
            for (JsonElement entry : entries) {
                String name = entry.getAsString();
                orders.put(name, i);
                i++;
            }
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            if (!"json".equals(FilenameUtils.getExtension(fileName)) || "@order.json".equals(fileName)) {
                continue;
            }
            try (FileReader reader = new FileReader(file)) {
                JsonElement rootElement = parser.parse(reader);
                ComponentGroup group = new ComponentGroup();
                group.setup(rootElement);
                groups.add(group);
            }
        }

        groups.sort(Comparator.comparingInt(group -> orders.getOrDefault(group.getRegistryName().toString(), 0)));
    }

    private static void categorizeTypes() {
        for (ComponentGroup group : groups) {
            groupedTypes.addAll(group.members);
        }
        for (IProcedureType<?> type : StevesFactoryManagerAPI.getProceduresRegistry().getValues()) {
            if (!groupedTypes.contains(type) && Procedures.isEnabled(type)) {
                ungroupedTypes.add(type);
            }
        }
    }

    private ResourceLocation registryName;
    private ResourceLocation icon;
    private String translationKey;
    private List<IProcedureType<?>> members = new ArrayList<>();

    private ComponentGroup() {
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Nullable
    public ResourceLocation getIcon() {
        return icon;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public List<IProcedureType<?>> getMembers() {
        return members;
    }

    private void setup(JsonElement rootElement) {
        if (rootElement.isJsonObject()) {
            JsonObject root = (JsonObject) rootElement;
            processName(root.get("name"));
            processIcon(root.get("icon"));
            processTranslationKey(root.get("translation_key"));
            processMembers(root.get("members"));
        }
    }

    private void processName(JsonElement nameElement) {
        String name = nameElement.getAsString();
        registryName = new ResourceLocation(name);
    }

    private void processIcon(JsonElement iconElement) {
        if (iconElement != null) {
            String iconPath = iconElement.getAsString();
            icon = new ResourceLocation(iconPath);
        }
    }

    private void processTranslationKey(JsonElement translationKeyElement) {
        if (translationKeyElement != null) {
            translationKey = translationKeyElement.getAsString();
        } else {
            translationKey = registryName.toString();
        }
    }

    private void processMembers(JsonElement membersElement) {
        JsonArray members = membersElement.getAsJsonArray();
        for (JsonElement memberElement : members) {
            ResourceLocation member = new ResourceLocation(memberElement.getAsString());
            IProcedureType<?> type = StevesFactoryManagerAPI.getProceduresRegistry().getValue(member);
            if (type != null && Procedures.isEnabled(type)) {
                this.members.add(type);
            }
        }
    }
}
