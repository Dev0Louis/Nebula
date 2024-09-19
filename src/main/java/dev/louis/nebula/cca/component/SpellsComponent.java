package dev.louis.nebula.cca.component;

import dev.louis.nebula.Nebula;
import dev.louis.nebula.api.spell.Spell;
import dev.louis.nebula.api.spell.SpellType;
import dev.louis.nebula.world.SpellWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.load.ServerLoadAwareComponent;
import org.ladysnake.cca.api.v3.component.load.ServerUnloadAwareComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class SpellsComponent implements CommonTickingComponent, ServerLoadAwareComponent, ServerUnloadAwareComponent {

    private static final String SPELLS = "spells";
    private static final String SPELL_ID = "spell_id";
    private static final String SPELL_DATA = "spell_data";

    private World world;
    private final Chunk chunk;

    @Nullable //Null if spells were delegated to the world
    private Collection<Spell> spells = new ArrayList<>();

    public SpellsComponent(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        var spellsNbt = tag.getList(SPELLS, NbtElement.COMPOUND_TYPE);
        Collection<Spell> spells = new ArrayList<>(spellsNbt.size());
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
                                spells.add(spell);
                            },
                            () -> Nebula.LOGGER.error("Unknown spellType " + spellId + ". Skipping.")
                    );
        }
        this.spells = spells;
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList nbtList = new NbtList();
        if (spells != null) {
            for (Spell spell : spells) {
                var spellNbt = new NbtCompound();
                spellNbt.putString(SPELL_ID, spell.getType().id().toString());
                spellNbt.put(SPELL_DATA, spell.writeNbt(new NbtCompound()));
                nbtList.add(spellNbt);
            }
        }

        tag.put(SPELLS, nbtList);
    }

    @Override
    public void tick() {
        //this.assertWorldAccess();
    }

    private void assertWorldAccess() {
        if (world == null) {
            this.world = switch (chunk) {
                case WorldChunk worldChunk -> worldChunk.getWorld();
                default -> throw new IllegalArgumentException("Spells Component needs a world access. Not provided by " + chunk.getClass().getSimpleName());
            };
        }
    }

    @Override
    public void loadServerside() {
        this.assertWorldAccess();
        int failedStarts = 0;
        if (spells == null) throw new IllegalStateException("Spells is null on load?");
        
        for (Spell spell : spells) {
            if (!((SpellWorld) world).nebula$startSpell(this.chunk, spell)) {
                failedStarts++;
            }
        }
        if (failedStarts > 0) {
            var spellOrSpells = failedStarts == 1 ? "spell" : "spells";
            Nebula.LOGGER.warn("Failed to start " + failedStarts + " " + spellOrSpells + " from Chunk " + chunk.getPos());
        }
        spells = null;
    }

    @Override
    public void unloadServerside() {
        this.assertWorldAccess();

        this.spells = ((SpellWorld) world).nebula$takeSpells(this.chunk);
    }

    public void castSpell(Spell spell) {
        ((SpellWorld) world).nebula$startSpell(this.chunk, spell);
    }
}
