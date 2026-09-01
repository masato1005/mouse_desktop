package common.EventType;

import java.awt.Dimension;

import common.data.MouseData;

public enum WallType {
    NORTH {
        @Override
        public boolean isTouchWall(MouseData point, int WALL_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseY() <= WALL_RANGE;
        }

        @Override
        public boolean checkEscapeCoolTimeArea(MouseData point, int WALL_COOL_TIME_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseY() > WALL_COOL_TIME_RANGE;
        }
    },
    SOUTH {
        @Override
        public boolean isTouchWall(MouseData point, int WALL_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseY() >= SCREEN_SIZE.height - WALL_RANGE;
        }

        @Override
        public boolean checkEscapeCoolTimeArea(MouseData point, int WALL_COOL_TIME_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseY() < SCREEN_SIZE.height - WALL_COOL_TIME_RANGE;
        }
    },
    WEST {
        @Override
        public boolean isTouchWall(MouseData point, int WALL_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseX() <= WALL_RANGE;
        }

        @Override
        public boolean checkEscapeCoolTimeArea(MouseData point, int WALL_COOL_TIME_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseX() > WALL_COOL_TIME_RANGE;
        }
    },
    EAST {
        @Override
        public boolean isTouchWall(MouseData point, int WALL_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseX() >= SCREEN_SIZE.width - WALL_RANGE;
        }

        @Override
        public boolean checkEscapeCoolTimeArea(MouseData point, int WALL_COOL_TIME_RANGE, Dimension SCREEN_SIZE) {
            return point.getMouseX() < SCREEN_SIZE.width - WALL_COOL_TIME_RANGE;
        }
    };

    public abstract boolean isTouchWall(MouseData point, int WALL_RANGE, Dimension SCREEN_SIZE);

    public abstract boolean checkEscapeCoolTimeArea(MouseData point, int WALL_COOL_TIME_RANGE, Dimension SCREEN_SIZE);
}
