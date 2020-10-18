import React, { useState, useEffect } from 'react'
import {
    Grid, Typography, Fab, Button, Paper, MenuList, MenuItem, Popover, CircularProgress
} from '@material-ui/core'
import CreateIcon from '@material-ui/icons/Create'
import Cookie from 'js-cookie'
import { useRouter } from 'next/router'
import SortIcon from '@material-ui/icons/Sort'
import SwapVertIcon from '@material-ui/icons/SwapVert'
import CreateNoteMenu from '../components/notes/CreateNoteMenu'
import NotePreview from '../components/notes/NotePreview'
import Navbar from '../components/navbar/Navbar'
import SearchNotes from '../components/notes/SearchNotes'
import CreateNotePreview from '../components/notes/CreateNotePreview'
import NotesRightClickMenu from '../components/utils/NotesRightClickMenu'

//todo display user's actual notes
export default function Notes() {
    const router = useRouter()

    const [ menuOpen, setMenuOpen ] = useState(false)
    const [ notes, setNotes ] = useState([])

    //l short for loading (i'm cutting it short)
    const [ notesLoading, setNotesLoading ] = useState('l')

    /*eslint-disable*/
    const [ userNotes, setUserNotes ] = useState([])
    const [ sharedNotes, setSharedNotes ] = useState([])
    /*eslint-enable*/

    const [ showSortMenu, setShowSortMenu ] = useState(false)
    const [ sortMenuAnchor, setSortMenuAchor] = useState(null)

    const [ orderDesc, setOrderDesc ] = useState(true)

    const [ principalUsername, setPrincipalUsername ] = useState('nottte-loading') //todo don't let people make nottte-loading their name lol

    const [ backupNotes, setBackupNotes ] = useState([]) //used for reverting after search

    const [ showNotes, setShowNotes ] = useState(true)
    const [ sortedBy, setSortedBy ] = useState('last-modified')

    const jwt = Cookie.get('jwt')

    const parseJwt = token => {
        const base64Url = token.split('.')[1]
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(
            c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
        ).join(''))

        return JSON.parse(jsonPayload)
    }

    const orderNotesByLastModified = (currentNotes = notes.slice(), desc = true) => {
        if (desc) {
            currentNotes.sort((a, b) => new Date(b.lastModified) - new Date(a.lastModified))
        } else {
            currentNotes.sort((a, b) => new Date(a.lastModified) - new Date(b.lastModified))
        }
        setNotes(currentNotes)
        return currentNotes
    }

    const getNotesFromServer = async () => {
        //setting it in a variable because returning in lambdas doesn't return the outer function
        let needToReturn = false
        const userNotesResponse = await fetch('http://localhost:8080/api/v1/notes/principal/notes', {
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }).catch(() => {
            setNotesLoading('e')
            needToReturn = true
        })
        if (needToReturn) {
            return
        }
        const userNotesText = await userNotesResponse.text()
        const userNotesParsed = JSON.parse(userNotesText)
        setUserNotes(userNotesParsed)

        const sharedNotesResponse = await fetch('http://localhost:8080/api/v1/shares/principal/shared-notes', {
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }).catch(() => {
            setNotesLoading('e')
            needToReturn = true
        })
        if (needToReturn) {
            return
        }
        const sharedNotesText = await sharedNotesResponse.text()
        const sharedNotesParsed = JSON.parse(sharedNotesText)
        setSharedNotes(sharedNotesParsed)

        const combinedNotes = [...userNotesParsed, ...sharedNotesParsed]

        //sorting by last modified and that assigns 'notes' to the new value
        setBackupNotes(orderNotesByLastModified(combinedNotes))
        setNotesLoading('d') //short for done
    }

    useEffect(() => {
        if (!jwt) {
            router.push('/login') //todo redirect back to /notes after login
        } else {
            setPrincipalUsername(parseJwt(jwt).sub)
            getNotesFromServer()
        }
    }, [ jwt ])

    const orderNotesByTitle = (currentNotes = notes.slice(), desc = true) => {
        if (desc) {
            currentNotes.sort((a, b) => (a.title > b.title ? 1 : -1))
        } else {
            currentNotes.sort((a, b) => (a.title < b.title ? 1 : -1))
        }
        setNotes(currentNotes)
    }

    const orderNotesByLastViewedByAuthor = (currentNotes = notes.slice(), desc = true) => {
        if (desc) {
            currentNotes.sort(
                (a, b) => new Date(b.lastViewedByAuthor) - new Date(a.lastViewedByAuthor)
            )
        } else {
            currentNotes.sort(
                (a, b) => new Date(a.lastViewedByAuthor) - new Date(b.lastViewedByAuthor)
            )
        }
        setNotes(currentNotes)
    }

    const orderNotesByLastViewed = (currentNotes = notes.slice(), desc = true) => {
        if (desc) {
            currentNotes.sort((a, b) => new Date(b.lastViewed) - new Date(a.lastViewed))
        } else {
            currentNotes.sort((a, b) => new Date(a.lastViewed) - new Date(b.lastViewed))
        }
        setNotes(currentNotes)
    }

    const handleCreateClick = () => {
        setMenuOpen(!menuOpen)
    }

    const handleNoteRename = (index, newName) => {
        const newNotes = notes.slice()
        newNotes[index].title = newName
        setNotes(newNotes)
    }

    const selectSortOption = option => {
        const currentNotes = notes.slice()
        if (option === 'last-modified') {
            orderNotesByLastModified(currentNotes, orderDesc)
        } else if (option === 'title') {
            orderNotesByTitle(currentNotes, orderDesc)
        } else if (option === 'last-viewed-by-author') {
            orderNotesByLastViewedByAuthor(currentNotes, orderDesc)
        } else if (option === 'last-viewed') {
            orderNotesByLastViewed(currentNotes, orderDesc)
        }
        setSortedBy(option)
        setShowSortMenu(false)
        console.log(option)
    }

    const handleOrderSwap = () => {
        setOrderDesc(!orderDesc)
        setNotes(notes.reverse())
    }

    const handleSearch = value => {
        const notesCopy = backupNotes.slice()
        // eslint-disable-next-line no-param-reassign
        value += ''
        setNotes(notesCopy.filter(note => (`${note.title}`).toLowerCase().includes(value.toLowerCase())))
    }

    const toggleShowNotes = () => {
        setShowNotes(!showNotes)
    }

    return (
        <div>
            <div style={{ marginTop: '10vh' }}>
                <Navbar />
                <div style={{
                    backgroundColor: 'white', margin: '0 auto', width: '100%', minHeight: '90vh'
                }}
                >
                    <div style={{
                        display: 'inline-flex', justifyContent: 'flex-end', margin: '0 auto', marginTop: 10, width: '97.5%'
                    }}
                    >
                        <div style={{ display: 'inline-flex', cursor: 'pointer' }}>
                            <Grid container spacing={3} alignItems='center' justify='center'>
                                <Grid item lg={6}>
                                    <SearchNotes
                                        handleSearch={value => handleSearch(value)}
                                        style={{ marginRight: 10 }}
                                    />
                                </Grid>
                                <Grid item lg={2}>
                                    <Button
                                        startIcon={<SortIcon />}
                                        onClick={e => {
                                            setSortMenuAchor(e.currentTarget)
                                            setShowSortMenu(!showSortMenu)
                                        }}
                                        style={{ marginLeft: 5, marginRight: 5 }}
                                    >
                                        Sort
                                    </Button>
                                </Grid>
                                <Grid item lg={2}>
                                    <Button
                                        startIcon={<SwapVertIcon className={orderDesc ? 'rotate' : 'unrotate'} />}
                                        onClick={handleOrderSwap}
                                    >
                                        {orderDesc ? 'Descending' : 'Ascending'}
                                    </Button>
                                </Grid>
                            </Grid>

                            <Popover
                                open={showSortMenu}
                                anchorEl={sortMenuAnchor}
                                disablePortal
                                anchorOrigin={{
                                    vertical: 'bottom',
                                    horizontal: 'left'
                                }}
                                onClose={() => setShowSortMenu(false)}
                            >
                                <Paper>
                                    <MenuList id='sort-menu'>
                                        <MenuItem onClick={() => selectSortOption('last-modified')} selected={sortedBy === 'last-modified'}>
                                            Last modified
                                        </MenuItem>
                                        <MenuItem onClick={() => selectSortOption('title')} selected={sortedBy === 'title'}>
                                            Title
                                        </MenuItem>
                                        <MenuItem
                                            onClick={() => selectSortOption('last-viewed-by-author')}
                                            selected={sortedBy === 'last-viewed-by-author'}
                                        >
                                            Last viewed by author
                                        </MenuItem>
                                        <MenuItem onClick={() => selectSortOption('last-viewed')} selected={sortedBy === 'last-viewed'}>
                                            Last viewed
                                        </MenuItem>
                                    </MenuList>
                                </Paper>
                            </Popover>

                        </div>
                    </div>
                    {/* notes */}
                    <div style={{ margin: 0 }}>
                        <Grid container spacing={3} style={{ margin: 0, width: '100%' }}>
                            <>
                                <Grid item xs={12} sm={6} md={4} lg={3} style={{ paddingTop: 3 }}>
                                    <CreateNotePreview jwt={jwt} onCreate={toggleShowNotes} />
                                </Grid>
                                {
                                    showNotes && notes.map((note, i) => (
                                        <React.Fragment key={note.id}>
                                            <Grid
                                                item
                                                xs={12}
                                                sm={6}
                                                md={4}
                                                lg={3}
                                                style={{ paddingTop: 3 }}
                                            >
                                                <NotePreview
                                                    name={note.title}
                                                    editorState={note.body}
                                                    jwt={jwt}
                                                    //eslint-disable-next-line
                                                    onNoteRename={newName => handleNoteRename(i, newName)}
                                                    shared={note.author !== principalUsername && principalUsername !== 'nottte-loading'}
                                                    author={note.author}
                                                />
                                            </Grid>
                                        </React.Fragment>
                                    ))

                                }

                                {
                                    (notesLoading === 'l' || notesLoading === 'e') && (
                                        <div
                                            style={{
                                                position: 'absolute',
                                                left: '50%',
                                                top: '50%',
                                                transform: 'translate(-50%, -50%)'
                                            }}
                                        >
                                            {
                                                notesLoading !== 'e'
                                                    ? <CircularProgress color='secondary' />
                                                    : <Typography variant='h4'>Failed to load</Typography>
                                            }
                                        </div>
                                    )
                                }
                            </>
                        </Grid>
                    </div>
                </div>
            </div>

            <div style={{
                position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)'
            }}
            >
                <CreateNoteMenu open={menuOpen} onClose={handleCreateClick} jwt={jwt} />
            </div>

            <NotesRightClickMenu onCreateNote={handleCreateClick} />

        </div>

    )
}
