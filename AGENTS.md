# TeamDex — Project Context

## What this is

Android Pokédex app built with Java. Fetches data from [PokéAPI](https://pokeapi.co/api/v2/) and lets users browse Pokémon, view details, and build/save teams. Auth via Firebase.

Package: `es.upm.mssde.pokedex`

## Tech stack

- **Android SDK** (Java, not Kotlin)
- **Retrofit** — HTTP client for PokéAPI calls
- **Picasso** — image loading and bitmap decoding
- **Firebase Auth** — login / sign-up / account deletion
- **SQLite via SQLiteOpenHelper** (`TeamDatabase.java`) — local team persistence
- **RecyclerView + GridLayoutManager** — Pokédex list (3 columns)
- **Observer pattern** (`MyObservable` / `MyObserver`) — decouples API layer from adapters

## Architecture

```
PokeAPI (data layer)
  └── implements MyObservable
  └── notifies MyObserver implementors on API responses

PokedexListAdapter / TeamBuilderListAdapter
  └── implement MyObserver
  └── update RecyclerView on data callbacks

Fragments (PokedexFragment, TeamViewerFragment)
  └── own the RecyclerView + adapter lifecycle
  └── handle menu / search / scroll events
```

## Key files

| File | Role |
|------|------|
| `PokeAPI.java` | All PokéAPI calls. Cumulative `poke_list` grows with each paginated batch. `allPokemonNames` holds the full name list (fetched once for search). |
| `PokedexListAdapter.java` | RecyclerView adapter for the Pokédex. Handles filter/unfilter, color cache, scroll-triggered pagination. |
| `PokedexFragment.java` | Owns the Pokédex RecyclerView. Triggers pagination on scroll and search on text change. |
| `TeamDatabase.java` | SQLite helper. Always call `deleteTeam` before `addTeam` to avoid orphaned rows. |
| `TeamBuilderActivity.java` | Save team flow: delete-then-insert. Persists `teamID` via `onSaveInstanceState` for rotation safety. |
| `IPokemonEndpoint.java` | Retrofit interface. `getAllPokemon(limit, offset)`, `getPokemon(id)`, `getPokemonSpecies(id)`. |
| `models/PokemonResult.java` | Name + URL. `getNum()` parses the Pokémon id from the URL. `getName()` capitalizes. |
| `models/Pokemon.java` | Full Pokémon detail (stats, height, weight, abilities). |
| `models/Species.java` | Species data. `getCaptureRate()` returns percentage (0–100), computed fresh each call. |

## Important constraints

- **Do NOT modify `.github/workflows/release_apk.yml`** — it works as-is and the user wants it left alone.
- Queries use parameterized `?` placeholders in `TeamDatabase` — never revert to string concatenation.
- `android assert` is a no-op at runtime — use real null checks instead.

## Search implementation

Search fetches all Pokémon names once at fragment creation (`getAllPokemon(10000, 0)`) and stores them in `PokeAPI.allPokemonNames`. Filtering is synchronous in-memory substring match. Falls back to locally-loaded `unfilteredData` while the fetch is still in flight.

## Pagination

`PokeAPI.poke_list` is cumulative — each batch is appended. `PokedexListAdapter.onPokemonsDataChanged` detects new items by comparing `pokemon_list.size()` with `unfilteredData.size()` and appends only the delta to avoid scroll-to-top on load.

## Color cache

`PokedexListAdapter.colorCache` (HashMap keyed by Pokémon number) stores the computed dominant card color. First bind launches a background thread; subsequent binds apply the cached color instantly. Race condition guarded with `getBindingAdapterPosition()` check before UI update.

## Versioning

On every release, update ALL of the following:

- `versionCode` in `pokedex/build.gradle` — integer, increment by 1
- `versionName` in `pokedex/build.gradle` — semver `major.minor.patch`
  - **patch**: bug fixes
  - **minor**: new features
  - **major**: architectural changes or breaking changes
- `TAG_VERSION.md` — must match `versionName` with a `v` prefix (e.g. `v1.3.1`)
- `CHANGELOG.md` — add a new entry at the top with the version and changes
- Release commit message: `V{versionName}` (e.g. `V1.3.1`)

## Build environment

- Test task: `./gradlew :pokedex:testDebugUnitTest`
- Unit tests are in `pokedex/src/test/java/es/upm/mssde/pokedex/models/`
