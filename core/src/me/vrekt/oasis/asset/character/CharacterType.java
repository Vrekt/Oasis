package me.vrekt.oasis.asset.character;

/**
 * Represents a character type
 */
public enum CharacterType {

    /**
     * Healer female
     */
    ATHENA("characters/athena/Athena.atlas");

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
