package com.inspiredandroid.braincup.games

/**
 * Hand-authored Prism Clear catalog (15 levels).
 *
 * Curve: L1 teaches cascade/order (not a free one-swap), mid levels tighten budget,
 * L9+ are dense “planning” boards where many legal swaps exist but only careful lines clear.
 * Levels beyond [COUNT] clamp to the last board.
 *
 * Board chars (whitespace ignored): `R` ruby, `G` emerald, `B` sapphire, `P` amethyst,
 * `O` topaz, `.` empty. Solutions are flat cell-index pairs for unit tests only.
 */
data class PrismClearLevel(
    val id: Int,
    val rows: Int,
    val cols: Int,
    val moveBudget: Int,
    val board: String,
    val solution: List<Pair<Int, Int>>,
) {
    fun parseCells(): Array<PrismTileType?> {
        val clean = board.filter { !it.isWhitespace() }
        require(clean.length == rows * cols) {
            "Prism Clear level $id board length ${clean.length} != ${rows * cols}"
        }
        return Array(rows * cols) { i ->
            when (val ch = clean[i]) {
                '.' -> null
                'R' -> PrismTileType.RUBY
                'G' -> PrismTileType.EMERALD
                'B' -> PrismTileType.SAPPHIRE
                'P' -> PrismTileType.AMETHYST
                'O' -> PrismTileType.TOPAZ
                else -> error("Prism Clear level $id: bad cell '$ch'")
            }
        }
    }
}

object PrismClearLevels {
    const val COUNT: Int = 15

    val all: List<PrismClearLevel> = listOf(
        // L1 — cascade teach: clear blues first, then the dual-seed bottom line
        level(
            1,
            3,
            6,
            3,
            """
            ......
            RBB...
            BRGRGG
            """,
            listOf(6 to 12, 14 to 15),
        ),
        // L2 — purple column must be “broken” into play before the base clears
        level(
            2,
            4,
            6,
            4,
            """
            .P....
            .P....
            .RGBB.
            RPBRGG
            """,
            listOf(13 to 19, 14 to 20, 20 to 21),
        ),
        // L3 — edge columns unlock the bottom zipper
        level(
            3,
            5,
            6,
            4,
            """
            ......
            B....P
            B....P
            R....G
            BRGRGP
            """,
            listOf(18 to 24, 23 to 29, 26 to 27),
        ),
        // L4 — interleaved mid-board; sequence of four precise swaps
        level(
            4,
            5,
            6,
            5,
            """
            ......
            ..PBO.
            ..BPO.
            ..GPG.
            RRBROG
            """,
            listOf(8 to 9, 20 to 26, 22 to 28, 26 to 27),
        ),
        // L5 — orange ladder sliding down the left edge
        level(
            5,
            6,
            6,
            5,
            """
            ......
            .O....
            PO....
            OP....
            BPBR..
            RRGBGG
            """,
            listOf(12 to 13, 24 to 25, 27 to 33, 32 to 33),
        ),
        // L6 — zero-slack mid density (budget = solution length)
        level(
            6,
            6,
            6,
            5,
            """
            ......
            ......
            .O.BBP
            .O.PBB
            .R.PGB
            ROGRBG
            """,
            listOf(16 to 17, 15 to 16, 25 to 31, 28 to 34, 32 to 33),
        ),
        // L7 — wider board; more legal swaps, still a short critical path
        level(
            7,
            6,
            7,
            5,
            """
            .......
            .......
            ..P.BB.
            O.P.BB.
            O.R.RG.
            ROPGBBG
            """,
            listOf(30 to 37, 32 to 39, 33 to 40, 35 to 36, 38 to 39),
        ),
        // L8 — gold approach: 7×7 with spaced “islands”
        level(
            8,
            7,
            7,
            6,
            """
            .......
            .......
            .......
            B..POB.
            B..PBPO
            R..RBGP
            BRGPGOP
            """,
            listOf(25 to 26, 33 to 34, 35 to 42, 38 to 45, 40 to 47, 44 to 45),
        ),
        // L9 — asymmetric pillars; later swaps need earlier setup pieces
        level(
            9,
            7,
            7,
            7,
            """
            .......
            .......
            ....P.P
            .BOOP.P
            .OBOB.G
            .RBPOBP
            .ORGRGB
            """,
            listOf(22 to 23, 34 to 41, 36 to 43, 38 to 39, 32 to 39, 41 to 48, 45 to 46),
        ),
        // L10 — gold: dense left stack; wrong early match soft-locks types
        level(
            10,
            7,
            7,
            7,
            """
            .......
            B......
            B.P.O..
            P.P.OOR
            B.R.BOR
            P.PBORG
            RPBGROG
            """,
            listOf(21 to 28, 30 to 37, 40 to 41, 40 to 47, 42 to 43, 45 to 46, 37 to 44),
        ),
        // L11 — “OROR” lattice: weave orange/ruby before bottom detonates
        level(
            11,
            7,
            7,
            7,
            """
            ..ORO..
            ..ORO.G
            ..BPR.G
            ..OBO.P
            ..BPB.G
            ..GPG.P
            RRBRBPG
            """,
            listOf(18 to 25, 27 to 34, 44 to 45, 25 to 32, 37 to 44, 47 to 48, 44 to 45),
        ),
        // L12 — twin blue columns force a double-setup before greens fall
        level(
            12,
            7,
            7,
            7,
            """
            ..GO.B.
            ..GO.B.
            ..PG.P.
            ..OOPB.
            ..POPRO
            ..BBGGR
            RRGRBPR
            """,
            listOf(16 to 17, 10 to 17, 19 to 26, 33 to 34, 32 to 33, 44 to 45, 38 to 45),
        ),
        // L13 — staggered greens + orange keystone on the right
        level(
            13,
            7,
            7,
            7,
            """
            ..G....
            ..GR...
            ..BR...
            .BGP..O
            OBBRPOP
            ORRPPOG
            BOBGRPG
            """,
            listOf(16 to 23, 31 to 38, 33 to 34, 40 to 41, 40 to 47, 42 to 43, 45 to 46),
        ),
        // L14 — compact expert: few moves for a full board (tight budget)
        level(
            14,
            7,
            7,
            6,
            """
            .B.....
            .B.PRR.
            .O.RGP.
            .BPOGP.
            .OOGBG.
            .ROPBP.
            .ORGRBG
            """,
            listOf(15 to 22, 25 to 32, 30 to 31, 36 to 37, 46 to 47, 47 to 48),
        ),
        // L15 — longest solution path: ten exact swaps, almost no slack
        level(
            15,
            7,
            7,
            10,
            """
            P...R..
            P.GOR..
            B.GRO..
            P.BGO..
            BOORB.B
            RPPOG.B
            BRGPBBG
            """,
            listOf(
                14 to 21,
                17 to 18,
                23 to 24,
                24 to 25,
                31 to 38,
                35 to 42,
                38 to 45,
                39 to 46,
                44 to 45,
                47 to 48,
            ),
        ),
    )

    init {
        require(all.size == COUNT)
        require(all.map { it.id } == (1..COUNT).toList())
    }

    /** Clamps to 1..[COUNT] so progress past the catalog re-plays the final board. */
    fun forLevel(level: Int): PrismClearLevel {
        val id = level.coerceIn(1, COUNT)
        return all[id - 1]
    }

    private fun level(
        id: Int,
        rows: Int,
        cols: Int,
        moveBudget: Int,
        board: String,
        solution: List<Pair<Int, Int>>,
    ): PrismClearLevel = PrismClearLevel(
        id = id,
        rows = rows,
        cols = cols,
        moveBudget = moveBudget,
        board = board.trimIndent(),
        solution = solution,
    )
}
