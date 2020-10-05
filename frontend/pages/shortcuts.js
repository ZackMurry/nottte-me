import React, { useState, useEffect } from 'react'
import { Paper, Typography, Grid } from '@material-ui/core'
import Head from 'next/head'
import { useRouter } from 'next/router'
import Cookie from 'js-cookie'
import TextShortcutPreview from '../components/shortcuts/text/TextShortcutPreview'
import CreateTextShortcut from '../components/shortcuts/text/CreateTextShortcut'
import Navbar from '../components/Navbar'
import NottteShortcutDisplay from '../components/shortcuts/NottteShortcutDisplay'
import CreateStyleShortcut from '../components/shortcuts/style/CreateStyleShortcut'
import StyleShortcutPreview from '../components/shortcuts/style/StyleShortcutPreview'
import PlainSnackbar from '../components/utils/PlainSnackbar'

//default text shortcuts
const nottteShortcuts = [
    {
        name: 'nottte-save',
        key: 's',
        description: "saves the current note (for when auto-saving isn't enough)"
    }
]

//todo help page
export default function Shortcuts() {
    const router = useRouter()

    const [ jwt ] = useState(Cookie.get('jwt'))

    const [ textShortcuts, setTextShortcuts ] = useState([])
    const [ styleShortcuts, setStyleShortcuts ] = useState([{ attributes: [{ attribute: '', value: '' }] }])
    const [ error, setError ] = useState('')
    const [ showError, setShowError ] = useState(false)

    const getShortcuts = async () => {
        const requestOptions = {
            headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + jwt }
        }

        //todo check for bad response codes

        //getting text shortcuts
        const textResponse = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/text-sorted', requestOptions)
        const textText = await textResponse.text()
        setTextShortcuts(JSON.parse(textText))

        //getting style shortcuts
        const styleResponse = await fetch('http://localhost:8080/api/v1/users/principal/preferences/shortcuts/style-sorted', requestOptions)
        const styleText = await styleResponse.text()
        console.log(styleText)
        setStyleShortcuts(JSON.parse(styleText))
    }

    useEffect(() => {
        if (jwt) {
            getShortcuts()
        } else {
            //todo redirect back here once logged in
            router.push('/login')
        }
    }, [])

    //find current index of shortcut
    //binary searching because it's alphabetically sorted
    const binarySearchShortcuts = (array, name) => {
        let start = 0
        let stop = array.length - 1
        let middle = Math.floor((stop + start) / 2)

        while (array[middle].name !== name && start < stop) {
            //adjusting search area
            if (name < array[middle].name) {
                stop = middle - 1
            } else if (name > array[middle].name) {
                start = middle + 1
            }

            //recalculate middle
            middle = Math.floor((stop + start) / 2)
        }
        return (array[middle].name !== name) ? -1 : middle
    }

    const updateTextShortcut = (name, key, text, alt) => {
        const index = binarySearchShortcuts(textShortcuts, name)
        const updatedTextShortcuts = textShortcuts.slice()
        updatedTextShortcuts.splice(index, 1, {
            name,
            key,
            text,
            alt
        })
        setTextShortcuts(updatedTextShortcuts)
    }

    const updateStyleShortcut = (name, key, attributes, alt) => {
        const index = binarySearchShortcuts(styleShortcuts, name)
        const updatedStyleShortcuts = styleShortcuts.slice()
        updatedStyleShortcuts.splice(index, 1, {
            name,
            key,
            attributes,
            alt
        })
        setStyleShortcuts(updatedStyleShortcuts)
    }

    const deleteStyleShortcut = name => {
        const index = binarySearchShortcuts(styleShortcuts, name)
        const updatedStyleShortcuts = styleShortcuts.slice()
        updatedStyleShortcuts.splice(index, 1)
        setStyleShortcuts(updatedStyleShortcuts)
    }

    const deleteTextShortcut = name => {
        const index = binarySearchShortcuts(textShortcuts, name)
        const updatedTextShortcuts = textShortcuts.slice()
        updatedTextShortcuts.splice(index, 1)
        setTextShortcuts(updatedTextShortcuts)
    }

    return (
        <div>
            <Head>
                <title>Shortcuts | nottte.me</title>
            </Head>
            <div>
                <Navbar />
            </div>

            <Paper
                style={{
                    margin: '0 auto',
                    marginTop: '20vh',
                    // todo just do margin: '20vh auto' in all of the papers like this
                    marginBottom: '20vh',
                    width: '50%',
                    minHeight: '120vh',
                    paddingBottom: '10vh',
                    borderRadius: 40,
                    boxShadow: '5px 5px 10px black',
                    minWidth: 750
                }}
            >
                <Typography variant='h2' style={{ textAlign: 'center', paddingTop: '2.5vh', marginBottom: '2.5vh' }}>
                    Shortcuts
                </Typography>
                <div style={{ margin: '5% 10%' }}>

                    {/* text shortcuts */}
                    <Typography variant='h4' style={{ textAlign: 'center', marginBottom: '3.5vh', fontWeight: 300 }}>
                        Text shortcuts
                    </Typography>
                    <Typography
                        variant='h6'
                        style={{
                            padding: 0,
                            fontWeight: 100,
                            textAlign: 'center',
                            width: '80%',
                            marginLeft: 'auto',
                            marginRight: 'auto',
                            marginBottom: '3vh'
                        }}
                    >
                        Text shortcuts insert text at your cursor when you press the combination of
                        keys that activate them
                    </Typography>

                    {/* if user doesn't have any text shortcuts, show this */}
                    {
                        textShortcuts.length === 0 && (
                            <Typography variant='h6' style={{ textAlign: 'center', fontWeight: 700, marginBottom: '3vh' }}>
                                You don't have any text shortcuts. You should make some!
                            </Typography>
                        )
                    }

                    <Grid container spacing={3} style={{ marginBottom: '3vh' }}>
                        {
                            textShortcuts.length !== 0 && (
                                <Grid item xs={12}>
                                    {/* labels of columns at the top —
                                    just a custom TextShortcutEditor */}
                                    <Grid container spacing={3}>
                                        <Grid item xs={4}>
                                            <Typography variant='h6' style={{ fontWeight: 700 }}>name</Typography>
                                        </Grid>
                                        <Grid item xs={2}>
                                            <Typography variant='h6' style={{ fontWeight: 700 }}>shortcut</Typography>
                                        </Grid>
                                        <Grid item xs={6}>
                                            <Typography variant='h6' style={{ fontWeight: 700 }}>text</Typography>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            )
                        }

                        {
                            textShortcuts && textShortcuts.map(textShortcut => (
                                    <Grid item xs={12} key={textShortcut.name}>
                                        <TextShortcutPreview
                                            name={textShortcut.name}
                                            button={textShortcut.key}
                                            text={textShortcut.text}
                                            key={textShortcut.name}
                                            alt={textShortcut.alt}
                                            // eslint-disable-next-line
                                            update={(name, key, text, alt) => updateTextShortcut(name, key, text, alt)}
                                            jwt={jwt}
                                            onError={err => setError(err)}
                                            showError={show => setShowError(show)}
                                            deleteSelf={() => deleteTextShortcut(textShortcut.name)}
                                        />
                                    </Grid>
                            ))
                        }

                        <Grid item xs={12}>
                            <Typography variant='h6' style={{ textAlign: 'center' }}>
                                Create custom text shortcut
                            </Typography>
                        </Grid>
                        <Grid item xs={12}>
                            <CreateTextShortcut jwt={jwt} />
                        </Grid>
                    </Grid>

                    {/* style shortcuts */}
                    <Typography
                        variant='h4'
                        style={{
                            textAlign: 'center',
                            marginBottom: '3.5vh',
                            marginTop: '2.5vh',
                            fontWeight: 300
                        }}
                    >
                        Style shortcuts
                    </Typography>
                    <Typography
                        variant='h6'
                        style={{
                            padding: 0,
                            fontWeight: 100,
                            textAlign: 'center',
                            width: '80%',
                            marginLeft: 'auto',
                            marginRight: 'auto',
                            marginBottom: '3vh'
                        }}
                    >
                        Style shortcuts change CSS attributes when you activate them.
<br />
                        Changing these will also change their past usages
                    </Typography>

                    {/* if user doesn't have any style shortcuts, show this */}
                    {
                        styleShortcuts.length === 0 && (
                            <Typography variant='h6' style={{ textAlign: 'center', fontWeight: 700, marginBottom: '3vh' }}>
                                You don't have any style shortcuts. You should make some!
                            </Typography>
                        )
                    }
                    <Grid container spacing={3}>

                        {/* labels of columns */}
                        {
                            styleShortcuts.length !== 0 && (
                                <Grid item xs={12}>
                                    <Grid container spacing={3}>
                                        <Grid item xs={3}>
                                            <Typography variant='h6' style={{ fontWeight: 700 }}>name</Typography>
                                        </Grid>
                                        <Grid item xs={2}>
                                            <Typography variant='h6' style={{ fontWeight: 700 }}>shortcut</Typography>
                                        </Grid>
                                        <Grid item xs={3}>
                                            <Typography variant='h6' style={{ fontWeight: 700 }}>attribute</Typography>
                                        </Grid>
                                        <Grid item xs={3}>
                                            <Typography variant='h6' style={{ fontWeight: 700 }}>value</Typography>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            )
                        }
                        {
                            styleShortcuts && styleShortcuts.map(styleShortcut => (
                                    <Grid item xs={12} key={styleShortcut.name}>
                                        <StyleShortcutPreview
                                            name={styleShortcut.name}
                                            button={styleShortcut.key}
                                            attributes={styleShortcut.attributes}
                                            alt={styleShortcut.alt}
                                            //eslint-disable-next-line
                                            update={(name, key, attributes, alt) => updateStyleShortcut(name, key, attributes, alt)}
                                            jwt={jwt}
                                            onError={err => setError(err)}
                                            showError={show => setShowError(show)}
                                            //eslint-disable-next-line
                                            deleteSelf={() => deleteStyleShortcut(styleShortcut.name)}
                                        />
                                    </Grid>
                            ))
                        }

                        <Grid item xs={12}>
                            <Typography variant='h6' style={{ textAlign: 'center' }}>
                                Create custom style shortcut
                            </Typography>
                        </Grid>
                        <Grid item xs={12}>
                            <CreateStyleShortcut jwt={jwt} />
                        </Grid>

                    </Grid>

                    {/* default shortcuts */}
                    <Typography
                        variant='h4'
                        style={{
                            textAlign: 'center', marginBottom: '3.5vh', marginTop: '5vh', fontWeight: 300
                        }}
                    >
                        Reserved shortcuts
                    </Typography>
                    <Typography
                        variant='h6'
                        style={{
                            padding: 0,
                            fontWeight: 100,
                            textAlign: 'center',
                            width: '80%',
                            marginLeft: 'auto',
                            marginRight: 'auto',
                            marginBottom: '3.5vh'
                        }}
                    >
                        These are shortcuts that are automatically added with nottte.me.
                        They pertain to complex functions that don't directly affect the editor,
                        so you can't delete or edit them.
                    </Typography>
                    <Grid container spacing={3}>
                        {/* column labels */}
                        <Grid item xs={12}>
                            <Grid container spacing={3}>
                                <Grid item xs={4}>
                                    <Typography variant='h6' style={{ fontWeight: 700 }}>name</Typography>
                                </Grid>
                                <Grid item xs={2}>
                                    <Typography variant='h6' style={{ fontWeight: 700 }}>button</Typography>
                                </Grid>
                                <Grid item xs={6}>
                                    <Typography variant='h6' style={{ fontWeight: 700 }}>description</Typography>
                                </Grid>
                            </Grid>
                        </Grid>

                        {/* values */}
                        {
                            nottteShortcuts.map(nottteShortcut => (
                                    <Grid item xs={12} key={nottteShortcut.name}>
                                        <NottteShortcutDisplay
                                            name={nottteShortcut.name}
                                            button={nottteShortcut.key}
                                            description={nottteShortcut.description}
                                        />
                                    </Grid>
                            ))
                        }

                    </Grid>
                </div>
            </Paper>
            <PlainSnackbar
                message={error}
                duration={3000}
                value={showError}
                onClose={() => setShowError(!showError)}
            />
        </div>

    )
}
