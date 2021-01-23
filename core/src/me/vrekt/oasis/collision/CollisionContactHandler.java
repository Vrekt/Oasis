package me.vrekt.oasis.collision;

import com.badlogic.gdx.physics.box2d.*;
import me.vrekt.oasis.entity.player.EntityPlayer;

/**
 * A basic collision handler for any world.
 */
public final class CollisionContactHandler implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        // TODO
    }

    @Override
    public void endContact(Contact contact) {
        // TODO
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        final Fixture fixtureA = contact.getFixtureA();
        final Fixture fixtureB = contact.getFixtureB();

        // disable collisions between players
        if (fixtureA.getUserData() instanceof EntityPlayer
                && fixtureB.getUserData() instanceof EntityPlayer) contact.setEnabled(false);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
