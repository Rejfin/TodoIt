package dev.rejfin.todoit.utils

import dev.rejfin.todoit.models.TrophyModel
import dev.rejfin.todoit.models.UserModel

object TrophyList {
    fun getTrophyList(user: UserModel) = listOf(
        TrophyModel(
            id = 0,
            title = "I will figure it out",
            description = "create your first task (${if (user.allTask > 1) 1 else user.allTask}/1)",
            unlocked = user.allTask >= 1
        ),
        TrophyModel(
            id = 1,
            title = "It's not that hard",
            description = "create 10 tasks (${if (user.allTask > 10) 10 else user.allTask}/10)",
            unlocked = user.allTask >= 10
        ),
        TrophyModel(
            id = 2,
            title = "I even like it",
            description = "create 20 tasks (${if (user.allTask > 20) 20 else user.allTask}/20)",
            unlocked = user.allTask >= 20
        ),
        TrophyModel(
            id = 3,
            title = "Can't live without tasks",
            description = "create 50 tasks (${if (user.allTask > 50) 50 else user.allTask}/50)",
            unlocked = user.allTask >= 50
        ),
        TrophyModel(
            id = 4,
            title = "Obsessive planner",
            description = "create 100 tasks (${if (user.allTask > 100) 100 else user.allTask}/100)",
            unlocked = user.allTask >= 100
        ),
        TrophyModel(
            id = 5,
            title = "Shy guest",
            description = "be a member of at least one group (${if (user.groups.size > 1) 1 else user.groups.size}/1)",
            unlocked = user.groups.isNotEmpty()
        ),
        TrophyModel(
            id = 6,
            title = "Social butterfly",
            description = "be a member of at least five group (${if (user.groups.size > 5) 5 else user.groups.size}/5)",
            unlocked = user.groups.size >= 5
        ),
        TrophyModel(
            id = 7,
            title = "So it began",
            description = "complete your first task (${if (user.taskDone > 1) 1 else user.taskDone}/1)",
            unlocked = user.taskDone >= 1
        ),
        TrophyModel(
            id = 8,
            title = "Things are starting to come together",
            description = "complete 10 tasks (${if (user.taskDone > 10) 10 else user.taskDone}/10)",
            unlocked = user.taskDone >= 10
        ),
        TrophyModel(
            id = 9,
            title = "Busy gentleman",
            description = "complete 20 tasks (${if (user.taskDone > 20) 20 else user.taskDone}/20)",
            unlocked = user.taskDone >= 20
        ),
        TrophyModel(
            id = 10,
            title = "Task master",
            description = "complete 50 tasks (${if (user.taskDone > 50) 50 else user.taskDone}/50)",
            unlocked = user.taskDone >= 50
        ),
        TrophyModel(
            id = 11,
            title = "Life is a task",
            description = "complete 100 tasks (${if (user.taskDone > 100) 100 else user.taskDone}/100)",
            unlocked = user.taskDone >= 100
        ),
        TrophyModel(
            id = 12,
            title = "Guest",
            description = "reach lvl 2 (${if (user.xp >= 150) 2 else (user.xp / 150 + 1)}/2)",
            unlocked = user.xp >= 150
        ),
        TrophyModel(
            id = 13,
            title = "Newbie",
            description = "reach lvl 5 (${if (user.xp >= 600) 5 else (user.xp / 150 + 1)}/5)",
            unlocked = user.xp >= 600
        ),
        TrophyModel(
            id = 14,
            title = "Novice",
            description = "reach lvl 10 (${if (user.xp >= 1350) 10 else (user.xp / 150 + 1)}/10)",
            unlocked = user.xp >= 1350
        ),
        TrophyModel(
            id = 15,
            title = "Apprentice",
            description = "reach lvl 20 (${if (user.xp >= 2850) 20 else (user.xp / 150 + 1)}/20)",
            unlocked = user.xp >= 2850
        ),
        TrophyModel(
            id = 16,
            title = "Advanced",
            description = "reach lvl 30 (${if (user.xp >= 4350) 30 else (user.xp / 150 + 1)}/30)",
            unlocked = user.xp >= 4350
        ),
        TrophyModel(
            id = 17,
            title = "Expert",
            description = "reach lvl 40 (${if (user.xp >= 5850) 40 else (user.xp / 150 + 1)}/40)",
            unlocked = user.xp >= 5850
        ),
        TrophyModel(
            id = 18,
            title = "Master",
            description = "reach lvl 50 (${if (user.xp >= 7350) 50 else (user.xp / 150 + 1)}/50)",
            unlocked = user.xp >= 7350
        ),
    )
}