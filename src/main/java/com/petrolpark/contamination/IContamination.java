package com.petrolpark.contamination;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.petrolpark.PetrolparkTags;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;

/**
 * A specific instance of a contaminable object, with the specific Contaminants that object posseses.
 */
public interface IContamination<OBJECT, OBJECT_STACK> {

    public static Optional<IContamination<?, ?>> get(Object object) {
        return Contaminables.streamContaminables().map(c -> c.getContamination(object)).filter(Objects::nonNull).findFirst().map(c -> (IContamination<?, ?>)c);
    };

    /**
     * @param inputs
     * @param output
     * @see IContamination#perpetuate(Stream, Object, Function) If you have a faster way of getting the Contamination
     */
    public static void perpetuate(Stream<Object> inputs, Object output) {
        perpetuate(inputs, output, object -> get(object).orElse(null));
    };

    /**
     * @param <OBJECT> Type of the contaminable object
     * @param inputs
     * @param output
     * @param contaminationGetter
     */
    public static <OBJECT> void perpetuate(Stream<OBJECT> inputs, OBJECT output, Function<OBJECT, IContamination<?, ?>> contaminationGetter) {
        Object2DoubleMap<Contaminant> amounts = new Object2DoubleArrayMap<>();
        double totalAmount = inputs.map(contaminationGetter)
            .dropWhile(Objects::isNull)
            .mapToDouble(contamination -> {
                double amount = contamination.getAmount();
                contamination.streamAllContaminants().forEach(contaminant -> amounts.merge(contaminant, amount, Double::sum));
                return amount;
            }).sum();
        Optional.ofNullable(contaminationGetter.apply(output)).ifPresent(contamination -> 
            contamination.contaminateAll(
                amounts.object2DoubleEntrySet().stream()
                .filter(entry -> entry.getKey().isPreserved(entry.getDoubleValue() / totalAmount))
                .map(Object2DoubleMap.Entry::getKey)
            )
        );
    };

    public Contaminable<OBJECT, OBJECT_STACK> getContaminable();
    
    public OBJECT getType();

    public double getAmount();

    public void save();

    public boolean has(Contaminant contaminant);

    public boolean hasAnyContaminant();

    public boolean hasAnyExtrinsicContaminant();

    public Stream<Contaminant> streamAllContaminants();

    /**
     * Stream all Contaminants in this Contamination that:<ul>
     * <li>Are not {@link IntrinsicContaminants intrinsic}
     * <li>Have no children in this Contamination</ul>
     * Note that this is the minimum set of Contaminants needed to uniquely define a Contamination.
     * @return Distinct Stream of Contaminants 
     */
    public Stream<Contaminant> streamOrphanExtrinsicContaminants();

    public default Stream<Contaminant> streamShownContaminants() {
        Set<Contaminant> shownIfAbsent = IntrinsicContaminants.getShownIfAbsent(this);
        return streamAllContaminants().dropWhile(PetrolparkTags.Contaminants.HIDDEN::matches).dropWhile(shownIfAbsent::contains);
    };

    public default Stream<Contaminant> streamShownAbsentContaminants() {
        return IntrinsicContaminants.getShownIfAbsent(this).stream().dropWhile(this::has).dropWhile(PetrolparkTags.Contaminants.HIDDEN::matches);
    };

    public boolean contaminate(Contaminant contaminant);

    /**
     * Add several Contaminants, and 
     * @param contaminantsStream
     * @return
     */
    public boolean contaminateAll(Stream<Contaminant> contaminantsStream);

    /**
     * Remove a Contaminant and any {@link Contaminant#getChildren() children} it has that don't belong to another parent.
     * If the Contaminant has any parents in this Contamination, it will not be removed.
     * @param contaminant
     * @return Whether this Contamination changed
     * @see IContamination#decontaminateOnly(Contaminant) Don't remove children
     */
    public boolean decontaminate(Contaminant contaminant);

    /**
     * Remove a Contaminant, but not any of its children.
     * If the Contaminant has any parents in this Contamination, it will not be removed.
     * @param contaminant
     * @return Whether this Contamination changed (the Contaminant was removed)
     * @see IContamination#decontaminate(Contaminant) Remove all children
     */
    public boolean decontaminateOnly(Contaminant contaminant);

    /**
     * Remove all extrinsic Contaminants.
     * @return Whether this Contamination changed (whether it had any extrinsic Contaminants)
     */
    public boolean fullyDecontaminate();
};