import { IconButton, InputBase } from '@material-ui/core'
import React, { useState } from 'react'
import SearchIcon from '@material-ui/icons/Search'
import ClearIcon from '@material-ui/icons/Clear'
import PlainTooltip from '../utils/PlainTooltip'
import theme from '../theme'

export default function SearchNotes({ handleSearch, style }) {
    const [ searchValue, setSearchValue] = useState('')

    const handleKeyDown = e => {
        if (e.key === 'Enter') {
            handleSearch(e.target.value)
        } else {
            setSearchValue(e.target.value)
        }
    }

    const resetSearch = () => {
        setSearchValue('')
        handleSearch('')
    }

    return (
        <div style={{ display: 'inline-flex', ...style }}>
            <InputBase
                value={searchValue}
                placeholder='Search...'
                onKeyDown={handleKeyDown}
                onChange={e => setSearchValue(e.target.value)}
                style={{
                    width: '10vw',
                    minWidth: 200,
                    borderRadius: 5,
                    backgroundColor: '#f5f5f5',
                    height: '3.25vh',
                    marginTop: 12,
                    padding: 5,
                    marginRight: 5
                }}
            />

            <IconButton onClick={() => handleSearch(searchValue)}>
                <SearchIcon style={{ margin: 5 }} fontSize='small' />
            </IconButton>

            <PlainTooltip title='Reset search'>
                <IconButton onClick={resetSearch}>
                    <ClearIcon style={{ margin: 5, color: searchValue ? theme.palette.secondary.main : undefined }} fontSize='small' />
                </IconButton>
            </PlainTooltip>

        </div>

    )
}
