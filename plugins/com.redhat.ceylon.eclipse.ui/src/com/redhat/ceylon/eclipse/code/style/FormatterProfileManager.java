package com.redhat.ceylon.eclipse.code.style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import ceylon.formatter.options.FormattingOptions;
import ceylon.formatter.options.SparseFormattingOptions;

/**
 * The model for the set of available ceylon.formatter profiles.
 */
public class FormatterProfileManager extends Observable {

    /**
     * Represents a profile with a unique name and the SparseFormatterOptions
     */
    public static class Profile implements Comparable<Profile> {

        private String name;
        private FormattingOptions settings;
        private int order;
        private final int currentVersion;
        private int type;

        public boolean hasEqualSettings(SparseFormattingOptions otherSettings) {
            if (this.getSettings() != null) {
                return this.getSettings().equals(otherSettings);
            } else {
                return false;
            }
        }

        public Profile(String name, FormattingOptions options, int type,
                int order, int currentVersion) {

            this.name = name;
            this.settings = options;
            this.type = type;
            this.order = order;
            this.currentVersion = currentVersion;
        }

        public String getName() {
            return name;
        }

        public Profile rename(String name, FormatterProfileManager manager) {
            final String trimmed = name.trim();
            if (this.type == 0) {
                Profile newProfile = new Profile(trimmed, this.settings, 1,
                        this.order, this.currentVersion);
                manager.addProfile(newProfile);
                return newProfile;
            } else { // custom
                if (trimmed.equals(getName()))
                    return this;
                String oldName = this.getName();
                this.name = trimmed;
                manager.profileRenamed(this, oldName);
                return this;
            }
        }

        public FormattingOptions getSettings() {
            return settings;
        }

        public final int compareTo(Profile o) {
            if (this.type == 0) {
                if (o.type == 0) {
                    return this.order - (o.getOrder());
                } else {
                    return -1;
                }
            } else {

                if (o.type == 1) {
                    return getName().compareToIgnoreCase(o.getName());
                } else {
                    return 1;
                }
            }
        }

        private int getOrder() {
            return this.order;
        }

        public void setSettings(FormattingOptions options,
                FormatterProfileManager manager) {
            if (options == null)
                throw new IllegalArgumentException();
            this.settings = options;
            if (manager != null) {
                manager.profileChanged(this);
            }
        }

        public boolean isBuiltInProfile() {
            return type == 0;
        }

    }

    /**
     * The possible events for observers listening to this class.
     */
    public final static int SELECTION_CHANGED_EVENT = 1;
    public final static int PROFILE_DELETED_EVENT = 2;
    public final static int PROFILE_RENAMED_EVENT = 3;
    public final static int PROFILE_CREATED_EVENT = 4;
    public final static int SETTINGS_CHANGED_EVENT = 5;

    private final Map<String, Profile> profiles;

    private final List<Profile> profilesByName;

    /**
     * The currently selected profile.
     */
    private Profile selected;

    public final static String CEYLON_PROFILE = "default";

    public final static String DEFAULT_PROFILE = CEYLON_PROFILE;
    public static final int CEYLON_FORMATTER_VERSION = 1;

    public FormatterProfileManager(List<Profile> profiles, String activeProfile) {

        addBuiltinProfiles(profiles);

        this.profiles = new HashMap<String, Profile>();
        this.profilesByName = new ArrayList<Profile>();

        for (final Iterator<Profile> iter = profiles.iterator(); iter.hasNext();) {
            final Profile profile = iter.next();

            this.profiles.put(profile.getName(), profile);
            this.profilesByName.add(profile);
        }

        Collections.sort(profilesByName);

        if (activeProfile != null) {
            selected = this.profiles.get(activeProfile);
        } else {
            selected = getDefaultProfile();
        }
    }

    /**
     * Notify observers with a message. The message must be one of the
     * following:
     * 
     * @param message
     *            Message to send out
     *
     * @see #SELECTION_CHANGED_EVENT
     * @see #PROFILE_DELETED_EVENT
     * @see #PROFILE_RENAMED_EVENT
     * @see #PROFILE_CREATED_EVENT
     * @see #SETTINGS_CHANGED_EVENT
     */
    protected void notifyObservers(int message) {
        setChanged();
        notifyObservers(new Integer(message));
    }


    /**
     * Get an immutable list as view on all profiles, sorted alphabetically.
     * Unless the set of profiles has been modified between the two calls, the
     * sequence is guaranteed to correspond to the one returned by
     * <code>getSortedNames</code>.
     * 
     * @return a list of elements of type <code>Profile</code>
     *
     * @see #getSortedDisplayNames()
     */
    public List<Profile> getSortedProfiles() {
        return Collections.unmodifiableList(profilesByName);
    }

    /**
     * Get the names of all profiles stored in this profile manager, sorted
     * alphabetically. Unless the set of profiles has been modified between the
     * two calls, the sequence is guaranteed to correspond to the one returned
     * by <code>getSortedProfiles</code>.
     * 
     * @return All names, sorted alphabetically
     * @see #getSortedProfiles()
     */
    public String[] getSortedDisplayNames() {
        final String[] sortedNames = new String[profilesByName.size()];
        int i = 0;
        for (final Iterator<Profile> iter = profilesByName.iterator(); iter
                .hasNext();) {
            Profile curr = iter.next();
            sortedNames[i++] = curr.getName();
        }
        return sortedNames;
    }

    public Profile getProfile(String name) {
        return profiles.get(name);
    }

    public void clearAllSettings() {
        // TODO
    }

    public Profile getSelected() {
        return this.selected;
    }

    public void setSelected(Profile profile) {
        final Profile newSelected = profiles.get(profile.getName());
        if (newSelected != null && !newSelected.equals(this.selected)) {
            this.selected = newSelected;
            notifyObservers(SELECTION_CHANGED_EVENT);
        }
    }

    public boolean containsName(String name) {
        for (final Iterator<Profile> iter = profilesByName.iterator(); iter
                .hasNext();) {
            Profile curr = iter.next();
            if (name.equals(curr.getName())) {
                return true;
            }
        }
        return false;
    }

    public void addProfile(Profile profile) {
        final Profile oldProfile = profiles.get(profile.getName());
        if (oldProfile != null) {
            profiles.remove(oldProfile.getName());
            profilesByName.remove(oldProfile);
        }
        profiles.put(profile.getName(), profile);
        profilesByName.add(profile);
        Collections.sort(profilesByName);
        this.selected = profile;
        notifyObservers(PROFILE_CREATED_EVENT);
    }

    public boolean deleteSelected() {
        if (this.selected == null)
            return false;

        return deleteProfile(this.selected);
    }

    public boolean deleteProfile(Profile profile) {
        int index = profilesByName.indexOf(profile);

        profiles.remove(profile.getName());
        profilesByName.remove(profile);

        if (index >= profilesByName.size())
            index--;
        selected = profilesByName.get(index);

        notifyObservers(PROFILE_DELETED_EVENT);
        return true;
    }

    public void profileRenamed(Profile profile, String oldName) {
        profiles.remove(oldName);
        profiles.put(profile.getName(), profile);

        Collections.sort(profilesByName);
        notifyObservers(PROFILE_RENAMED_EVENT);
    }

    public void profileReplaced(Profile oldProfile, Profile newProfile) {
        profiles.remove(oldProfile.getName());
        profiles.put(newProfile.getName(), newProfile);
        profilesByName.remove(oldProfile);
        profilesByName.add(newProfile);
        Collections.sort(profilesByName);

        setSelected(newProfile);
        notifyObservers(PROFILE_CREATED_EVENT);
        notifyObservers(SELECTION_CHANGED_EVENT);
    }

    public void profileChanged(Profile profile) {
        notifyObservers(SETTINGS_CHANGED_EVENT);
    }

    private static List<Profile> addBuiltinProfiles(List<Profile> profiles) {
        final Profile ceylonProfile = new Profile(CEYLON_PROFILE,
                new FormattingOptions(), 0, 1, CEYLON_FORMATTER_VERSION);
        profiles.add(ceylonProfile);

        return profiles;
    }

    public Profile getDefaultProfile() {
        return getProfile(DEFAULT_PROFILE);
    }
}
