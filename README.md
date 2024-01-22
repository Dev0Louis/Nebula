# Nebula
It's a Library for creating Spells in all sorts of ways. Nebula adds Spell, Mana adds Client Syncing. <br>
If you consider Nebula or are stuck on anything feel free to reach out to me on my [Discord Server](https://discord.gg/r4nxHRcrZw). <br>
If you want full control you can override the SpellManager or ManaManagers!
With that you can basically change everything!<br> <b><sub>*Only one Mod can override a Manager.*</b></sub>

You can include Nebula in your **build.gradle**:

```gradle
repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = "https://api.modrinth.com/maven"
            }
        }
        filter {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    modImplementation "maven.modrinth:nebula:4.0.3"
}
```
# Tutorial
You can see the [Nebulo Test mod](https://github.com/Dev0Louis/Nebula/tree/master/nebulo), it does implement a Spell and shows you how to override the Managers. 

If you need any further help reach out to me on [Discord](https://discord.gg/9m5xv3qcdt)
<details><summary>Old</summary>

```java
public class SuicideSpell extends Spell {
    public SuicideSpell(SpellType<? extends Spell> spellType, PlayerEntity caster) {
        super(spellType, caster);
    }

    @Override
    public void cast() {
        getCaster().kill();
    }
}
```
### Registering the Spell
```java
        public static SpellType<SuicideSpell> SUICIDE = SpellType.register(new Identifier("yourmod", "suicide"), SpellType.Builder.create(SuicideSpell::new, 1));
```
We registered the Spell you need to replace "yourmod" with your modId. The Spell costs 1 Mana.
</details>
