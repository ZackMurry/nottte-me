import React from 'react'
import { Link, Paper, Typography } from '@material-ui/core'
import Navbar from '../../../components/Navbar'

// todo docs
export default function TextShortcut() {
    return (
        <div>
            <Navbar />
            <div style={{ marginTop: '15vh' }}>
                <Paper style={{
                    margin: '0 auto',
                    marginTop: '20vh',
                    width: '50%',
                    borderRadius: '40px 40px 0 0',
                    boxShadow: '5px 5px 10px black',
                    paddingBottom: '20vh'
                }}
                >
                <Typography variant='h1' style={{ padding: '3vh 0 3vh 0', textAlign: 'center' }}>
                    Text shortcuts
                </Typography>
                {/* content */}
                <div style={{ width: '80%', margin: '0 auto' }}>
                    <Typography style={{ textAlign: 'center' }}>
                    Text shortcuts are an important part of the note-taking process.
                    On this page, you'll learn what text shortcuts are,
                    how to create one, and how to use one.
                    </Typography>

                    {/* what are they */}
                    <div style={{ width: '80%', margin: '5vh auto' }}>
                        <Typography variant='h3' style={{ textAlign: 'center', marginBottom: '1vh' }}>
                            What are text shortcuts?
                        </Typography>
                        <Typography style={{ textAlign: 'center' }}>
                            Text shortcuts are keyboard shortcuts that you can use
                            for things that you would otherwise need to type out often.
                        </Typography>
                    </div>

                    <div style={{ width: '80%', margin: '5vh auto' }}>
                        <Typography variant='h3' style={{ textAlign: 'center', marginBottom: '1vh' }}>
                            How do you create one?
                        </Typography>
                        <Typography style={{ textAlign: 'center' }}>
                            You can create them at
                            <Link href='/shortcuts' style={{ textDecoration: 'underline', marginLeft: 3, color: 'black' }}>
                                nottte.me/shortcuts
                            </Link>
                            . You give it a name, which must be unique between both your text and style shortcuts,
                            a key, which will be used in conjunction with control and (optionally) alt,
                            and a text value, which is what will be inserted upon pressing it. To use alt with your shortcut,
                            simply hold alt while selecting your button bind.
                        </Typography>
                    </div>

                    <div style={{ width: '80%', margin: '5vh auto' }}>
                        <Typography variant='h3' style={{ textAlign: 'center', marginBottom: '1vh' }}>
                            How do you use one?
                        </Typography>
                        <Typography style={{ textAlign: 'center' }}>
                            On a note, simply press the buttons that you bound your text shortcut to
                            and your desired text will be inserted after your cursor. You can also
                            select text and use a text shortcut, which will replace the selected text
                            with the shortcut text.
                        </Typography>
                    </div>
                </div>
                </Paper>
            </div>
        </div>

    )
}
