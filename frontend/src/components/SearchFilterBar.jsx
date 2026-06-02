import { SearchIcon } from './Icons'

function SearchFilterBar({
  search,
  onSearchChange,
  difficulty,
  onDifficultyChange,
  category,
  onCategoryChange,
  difficulties,
  categories,
}) {
  return (
    <div className="filters-bar">
      <div className="input-wrap">
        <span className="input-icon">
          <SearchIcon />
        </span>
        <input
          className="search-input"
          type="search"
          placeholder="Search a challenge, topic, or keyword"
          value={search}
          onChange={(event) => onSearchChange(event.target.value)}
        />
      </div>

      <select
        className="select-input"
        value={difficulty}
        onChange={(event) => onDifficultyChange(event.target.value)}
      >
        {difficulties.map((item) => (
          <option key={item} value={item}>
            {item}
          </option>
        ))}
      </select>

      <select
        className="select-input"
        value={category}
        onChange={(event) => onCategoryChange(event.target.value)}
      >
        {categories.map((item) => (
          <option key={item} value={item}>
            {item}
          </option>
        ))}
      </select>
    </div>
  )
}

export default SearchFilterBar
