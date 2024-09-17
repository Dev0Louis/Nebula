package dev.louis.nebula.cca.component;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebula.api.world.SpellWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.ladysnake.cca.api.v3.component.load.ServerLoadAwareComponent;
import org.ladysnake.cca.api.v3.component.load.ServerUnloadAwareComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpellsComponent implements CommonTickingComponent, ServerLoadAwareComponent, ServerUnloadAwareComponent {

    private static final String SPELLS = "spells";
    private static final String SPELL_ID = "spell_id";
    private static final String SPELL_DATA = "spell_data";

    private final World world;
    private final Chunk chunk;

    // Data
    private List<Spell> spells = new ArrayList<>();

    private int i;

    public SpellsComponent(Chunk chunk) {
        this.chunk = chunk;
        this.world = switch (chunk) {
            case WorldChunk worldChunk -> worldChunk.getWorld();
            default -> null;
        };
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
       var spellsNbt = tag.getList(SPELLS, NbtElement.COMPOUND_TYPE);
       var lookup = registryLookup.createRegistryLookup();
        for(int i = 0; i < spellsNbt.size(); ++i) {
            NbtCompound spellNbt = spellsNbt.getCompound(i);
            var spellId = Identifier.tryParse(spellNbt.getString(SPELL_ID));
            lookup.getOptionalEntry(SpellType.REGISTRY_KEY, RegistryKey.of(SpellType.REGISTRY_KEY, spellId))
                    .flatMap(ref -> Optional.ofNullable(ref.value()))
                    .ifPresentOrElse(
                            spellType -> {
                                Spell spell = spellType.factory().create(world);
                                spell.readNbt(spellNbt.getCompound(SPELL_DATA));
                                this.spells.add(spell);
                            },
                            () -> Nebula.LOGGER.error("Unknown spellType " + spellId + ". Skipping.")
                    );
        }

    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList nbtList = new NbtList();

        for (Spell spell : spells) {
            var spellNbt = new NbtCompound();
            spellNbt.putString(SPELL_ID, spell.getType().id().toString());
            spellNbt.put(SPELL_DATA, spell.writeNbt(new NbtCompound()));
            nbtList.add(spellNbt);
        }

        tag.put(SPELLS, nbtList);
    }

    @Override
    public void tick() {
        this.assertWorldAccess();
    }

    private void assertWorldAccess() {
        if (world == null) throw new IllegalArgumentException("Spells Component needs a world access. Not provided by " + chunk.getClass().getSimpleName());
    }

    @Override
    public void loadServerside() {
        this.assertWorldAccess();
        for (Spell spell : spells) {
            ((SpellWorld) world).startSpell(this.chunk, spell);
        }
    }

    @Override
    public void unloadServerside() {
        this.assertWorldAccess();
        System.out.println(i);
    }

    public Spell takeSpell(int id) {
        var spell = spells.get(id);
        if (spell == null) throw new IllegalStateException("Tried to take sZpell from SpellsComponent that didn't exist.");
        spells.remove(id);
        return spell;
    }

    public void giveSpell(Spell spell) {
        if (spells.contains(spell)) throw new IllegalStateException("Tried to give spell but we already own that id.");
        spells.add(spell);
    }
}
