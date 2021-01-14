package me.vrekt.oasis.asset.character;

/**
 * Represents a character type
 */
public enum CharacterType {

    /**
     * Healer female
     */
    ATHENA("characters/healer_female/HealerFemale.atlas"),
    /**
     * Healer male
     */
    CRIMSON("characters/healer_male/HealerMale.atlas");

    /**
     * The asset path of the character
     */
    private final String assetPath;

    CharacterType(String assetPath) {
        this.assetPath = assetPath;
    }

    /**
     * @return the asset path
     */
    public String assetPath() {
        return assetPath;
    }
}
