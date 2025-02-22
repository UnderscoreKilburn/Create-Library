package com.petrolpark.util;

import java.util.List;
import java.util.Locale;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class Lang {
    
    public static String asId(String string) {
        return string.toLowerCase(Locale.ROOT);
    };

    public static Component shortList(List<? extends Component> elements, int maxTextWidth, Font font) {
        if (elements.isEmpty()) return Component.translatable("petrolpark.generic.list.none");
        if (elements.size() == 1) return elements.get(0);
        int namedElements = 1;
        Component namedList = elements.get(0), extendedList = namedList, list;
        do {
            list = extendedList;
            Component nextElement = elements.get(namedElements);
            namedElements++;
            if (namedElements < elements.size()) {
                namedList = Component.translatable("petrolpark.generic.list.comma", namedList, nextElement);
                extendedList = Component.translatable("petrolpark.generic.list.and_more", namedList, elements.size() - namedElements);
            } else {
                extendedList = Component.translatable("petrolpark.generic.list.and", namedList, nextElement);
            };
        } while (font.width(extendedList) < maxTextWidth && namedElements < elements.size());
        return list;
    };
};
