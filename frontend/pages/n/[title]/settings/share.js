import {
    Button, Paper, Popover, Typography
} from '@material-ui/core'
import Head from 'next/head'
import { useRouter } from 'next/router'
import React, { useEffect, useState } from 'react'
import Cookie from 'js-cookie'
import { CloudUpload } from '@material-ui/icons'
import Navbar from '../../../../components/navbar/Navbar'
import theme from '../../../../components/theme'
import SharedWithTable from '../../../../components/shares/SharedWithTable'
import LinkSharesTable from '../../../../components/shares/LinkSharesTable'

//todo list shared users
export default function share() {
    const router = useRouter()
    const { title } = router.query

    const jwt = Cookie.get('jwt')

    const [ targetUsername, setTargetUsername ] = useState('')
    const [ showMoreSharing, setShowMoreSharing ] = useState(false)
    const [ showMoreAnchorEl, setShowMoreAnchorEl ] = useState(null)
    const [ targetAccountExists, setTargetAccountExists] = useState('Loading...')

    /* eslint-disable */
    const [ sharedWith, setSharedWith ] = useState([])
    const [ linkShares, setLinkShares ] = useState([])
    /* eslint-enable */

    const getData = async () => {
        //getting who this note is shared with
        if (!jwt) return

        const requestOptions = {
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }

        const response = await fetch(`http://localhost:8080/api/v1/shares/principal/note/${encodeURI(title)}/shares`, requestOptions)
        if (response.status !== 200) {
            console.log(response.status)
            return
        }
        const text = await response.text()
        console.log(text)
        setSharedWith(JSON.parse(text))
    }

    useEffect(() => {
        if (title) {
            getData()
        }
    }, [ title ])

    const handleShareWithUser = async () => {
        setShowMoreSharing(false)

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }

        const response = await fetch(`http://localhost:8080/api/v1/shares/principal/share/${title}/${targetUsername}`, requestOptions)

        if (response.status === 200) {
            router.reload() //could also just add it to the list
        }
        //todo show user different error conditions
    }

    const handleEnter = async event => {
        if (event.key === 'Enter') {
            setShowMoreSharing(true)
            const requestOptions = {
                headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
            }

            const response = await fetch('http://localhost:8080/api/v1/users/exists/' + encodeURI(targetUsername), requestOptions)
            const text = await response.text()
            setTargetAccountExists(
                text === 'true'
                    ? "This user's account exists. You're good to go!"
                    : "We couldn't find a user with this username. Double-check the username."
            )
        }
    }

    const removeShareFromArray = removedUsername => {
        const updatedSharedWith = sharedWith.slice()
        const index = updatedSharedWith.indexOf(removedUsername)
        if (index === -1) {
            console.log('index should not be -1.')
            return
        }
        updatedSharedWith.splice(index, 1)
        setSharedWith(updatedSharedWith)
    }

    return (
        <div>
            <Head>
                <title>
                    share
                    {` ${title} `}
                    | nottte.me
                </title>
            </Head>
            <div style={{ marginTop: 0 }}>
                <Navbar />
            </div>

            {/* main login */}
            <Paper
                style={{
                    margin: '20vh auto',
                    width: '50%',
                    minHeight: '120vh',
                    paddingBottom: '10vh',
                    borderRadius: 40,
                    boxShadow: '5px 5px 10px black',
                    minWidth: 75
                }}
            >
                <Typography variant='h1' style={{ textAlign: 'center', padding: '2vh 0' }}>
                    Share
                </Typography>
                <div style={{ width: '60%', margin: '0 auto' }}>
                    <div style={{ marginBottom: '2vh' }}>
                        <Typography variant='h5'>
                            Note:
                        </Typography>
                        <Typography style={{ fontSize: 18 }}>
                            Sharing notes is currently in beta. Currently, you can share notes
                            with other users,
                            but they won't be able to edit them. This feature is being developed,
                            so you can
                            hope for it in the future.
                        </Typography>
                    </div>

                    <div>
                        <Typography variant='h5'>
                            Share by username
                        </Typography>

                        <Typography style={{ fontSize: 18 }}>
                            Just type in your friend's username and hit enter
                        </Typography>

                        <div style={{ display: 'inline-flex' }}>
                            <Typography style={{ fontSize: 20, alignSelf: 'center', cursor: 'default' }}>
                                Share with
                            </Typography>
                            <input
                                aria-label='username'
                                type='text'
                                value={targetUsername}
                                onChange={e => setTargetUsername(e.target.value)}
                                style={{
                                    border: 'none',
                                    fontSize: 20,
                                    padding: 10,
                                    paddingLeft: 5,
                                    textDecoration: 'underline',
                                    width: '12.5vw'
                                }}
                                placeholder='username'
                                onKeyPress={event => handleEnter(event)}
                                autoCorrect='false'
                                spellCheck='false'
                                onClick={e => setShowMoreAnchorEl(e.currentTarget)}
                            />

                            <Popover
                                id='share-username-popover'
                                open={showMoreSharing}
                                anchorEl={showMoreAnchorEl}
                                onClose={() => setShowMoreSharing(false)}
                                anchorOrigin={{
                                    vertical: 'bottom',
                                    horizontal: 'left'
                                }}
                                style={{ width: '70%' }}
                            >
                                <Paper
                                    elevation={3}
                                    style={{
                                        minWidth: '12.5vw',
                                        maxWidth: '25vw',
                                        minHeight: '15vh',
                                        backgroundColor: theme.palette.secondary.main
                                    }}
                                >
                                    <Typography
                                        variant='h5'
                                        color='primary'
                                        style={{ textAlign: 'center', padding: 10 }}
                                    >
                                        Share with
                                        {targetUsername}
                                    </Typography>
                                    <Typography color='primary' style={{ textAlign: 'center', padding: 5 }}>
                                        {targetAccountExists}
                                    </Typography>
                                    {
                                        targetAccountExists === "This user's account exists. You're good to go!" && (
                                        <div
                                            style={{
                                                width: '100%',
                                                display: 'flex',
                                                justifyContent: 'center',
                                                padding: '1vh 0'
                                            }}
                                        >
                                            <Button
                                                startIcon={<CloudUpload />}
                                                onClick={handleShareWithUser}
                                                variant='contained'
                                            >
                                                Share
                                            </Button>
                                        </div>
                                        )
                                    }
                                </Paper>
                            </Popover>
                        </div>
                    </div>
                </div>
                <div style={{ width: '60%', margin: '3vh auto' }}>
                    <SharedWithTable
                        sharedWith={sharedWith}
                        jwt={jwt}
                        title={title}
                        onUnshare={username => removeShareFromArray(username)}
                    />
                </div>

                <div style={{ width: '60%', margin: '3vh auto' }}>
                    <LinkSharesTable
                        linkShares={linkShares}
                        jwt={jwt}
                        title={title}
                    />
                </div>
            </Paper>
        </div>
    )
}
