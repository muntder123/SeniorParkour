package dev.core.api.structure;

public interface Identified {

    /**
     * @return The identifier name of the current object.
     */
    String getId();

    /**
     * @return The display name of the object to show for players.
     */
    String getDisplayName();

    /**
     * Updates the display name of the current identifiable instance.
     *
     * @param displayName New display name to set.
     */
    void setDisplayName(String displayName);

}
