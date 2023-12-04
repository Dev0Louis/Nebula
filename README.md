# Nebula
Nebula is a Library for Mods that want to implement Spells.

Or something different. The Mod aims to be very flexible so it is easy to work with.

It is important, that Nebula is still in Beta, so things will/might change. 


# Tutorial
## Creating your first Spell
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

## More to be added.
