package com.petrolpark.network;

import java.util.function.Function;

import com.petrolpark.Petrolpark;
import com.petrolpark.network.packet.C2SPacket;
import com.petrolpark.tube.BuildTubePacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PetrolparkMessages {
    private static SimpleChannel INSTANCE;
    private static int packetID = 0;

    private static int id() {
        return packetID++;
    };

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
            .named(Petrolpark.asResource("messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();
    
        INSTANCE = net;

        addC2SPacket(net, BuildTubePacket.class, BuildTubePacket::new);
    };

    // public static <T extends S2CPacket> void addS2CPacket(SimpleChannel net, Class<T> clazz, Function<FriendlyByteBuf, T> decoder) {
    //     net.messageBuilder(clazz, id(), NetworkDirection.PLAY_TO_CLIENT)
    //         .decoder(decoder)
    //         .encoder(T::toBytes)
    //         .consumerMainThread(T::handle)
    //         .add();
    // };

    public static <T extends C2SPacket> void addC2SPacket(SimpleChannel net, Class<T> clazz, Function<FriendlyByteBuf, T> decoder) {
        net.messageBuilder(clazz, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(decoder)
            .encoder(T::toBytes)
            .consumerMainThread(T::handle)
            .add();
    };

    public static void sendToServer(C2SPacket message) {
        INSTANCE.sendToServer(message);
    };

    // public static void sendToClient(S2CPacket message, ServerPlayer player) {
    //     INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    // };

    // public static void sendToAllClientsInDimension(S2CPacket message, ServerLevel level) {
    //     INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), message);
    // };

    // public static void sendToAllClients(S2CPacket message) {
    //     INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    // };

    // public static void sendToAllClientsNear(S2CPacket message, BlockSource location) {
    //     INSTANCE.send(PacketDistributor.NEAR.with(TargetPoint.p(location.x(), location.y(), location.z(), 32d, location.getLevel().dimension())), message);
    // };

    // public static void sendToClientsTrackingEntity(S2CPacket message, Entity trackedEntity) {
    //     INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> trackedEntity), message);
    // };

    // public static void sendToClientsTrackingChunk(S2CPacket message, LevelChunk chunk) {
    //     INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
    // };
};