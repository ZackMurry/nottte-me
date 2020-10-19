const validateTitle = title => {
    if (title.includes('%')) {
        return 'Your note title cannot contain a percent sign.'
    } if (title.length > 200) {
        return 'Your note title cannot be longer than 200 characers.'
    } if (title.includes('/')) {
        return 'Your note cannot contain a slash, since URLs are encoded using slashes.'
    }
    if (title.includes('\\')) {
        return 'Your note title cannot contain a back slash (\\).'
    }
    return ''
}

export default validateTitle
