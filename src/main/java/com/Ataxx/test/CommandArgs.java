package com.Ataxx.test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandArgs implements Iterable<String> {
    private final String optionString;
    private final String[] arguments;
    private boolean ok;
    private ArrayList<String> keyList = new ArrayList();
    private ArrayList<String> valueList = new ArrayList();
    private static final String SHORT_OPTION = "-[a-zA-Z0-9_#@+%]";
    private static final String LONG_OPTION = "--[-_a-zA-Z0-9]*";
    private static final String VALUE = "(?:=(\\(.*\\)|))?";
    private static final String REPEAT = "(?:\\{(\\d+)(,)?(\\d*)\\}(?::([1-9]\\d*))?|:([1-9]\\d*))?";
    private static final String OPTION = "(-[a-zA-Z0-9_#@+%]|--[-_a-zA-Z0-9]*)(?:=(\\(.*\\)|))?(?:\\{(\\d+)(,)?(\\d*)\\}(?::([1-9]\\d*))?|:([1-9]\\d*))?";
    private static final Pattern OPTION_PATTERN = Pattern.compile("(-[a-zA-Z0-9_#@+%]|--[-_a-zA-Z0-9]*)(?:=(\\(.*\\)|))?(?:\\{(\\d+)(,)?(\\d*)\\}(?::([1-9]\\d*))?|:([1-9]\\d*))?");
    private static final Pattern ARGUMENT = Pattern.compile("(--)|(--\\S+?)(?:=(.*))?|(-[^-].*)");
    private static final Pattern WS = Pattern.compile("\\s+");

    public CommandArgs(String optionString, String[] rawArgs) {
        this.optionString = optionString;
        this.arguments = rawArgs;
        this.ok = true;
        ArrayList<OptionSpec> specs = OptionSpec.parse(optionString);
        this.createRawOptionLists(rawArgs, specs);
        this.checkOptions(specs);
    }

    public String getOptionString() {
        return this.optionString;
    }

    public String[] getArguments() {
        return this.arguments;
    }

    public int number(String key) {
        int result = 0;
        Iterator i$ = this.iterator();

        while(i$.hasNext()) {
            String k = (String)i$.next();
            if (k.equals(key)) {
                ++result;
            }
        }

        return result;
    }

    public boolean contains(String key) {
        return this.keyList.contains(key);
    }

    /** @deprecated */
    @Deprecated
    public boolean containsKey(String key) {
        return this.contains(key);
    }

    public List<String> get(String key) {
        ArrayList<String> result = new ArrayList();

        for(int k = 0; k < this.keyList.size(); ++k) {
            if (((String)this.keyList.get(k)).equals(key)) {
                result.add(this.valueList.get(k));
            }
        }

        return result;
    }

    public String getFirst(String key) {
        return this.getFirst(key, (String)null);
    }

    public String getFirst(String key, String dflt) {
        int k = this.keyList.indexOf(key);
        return k == -1 ? dflt : (String)this.valueList.get(k);
    }

    public String getLast(String key, String dflt) {
        int k = this.keyList.lastIndexOf(key);
        return k == -1 ? dflt : (String)this.valueList.get(k);
    }

    public String getLast(String key) {
        return this.getLast(key, (String)null);
    }

    public int getInt(String key) {
        return this.getInt(key, 10);
    }

    public int getInt(String key, int radix) {
        if (!this.contains(key)) {
            throw new NoSuchElementException(key);
        } else {
            return this.getInt(key, radix, 0);
        }
    }

    public int getInt(String key, int radix, int dflt) {
        String val = this.getLast(key);
        if (val == null) {
            return dflt;
        } else if (radix == 16 && val != null && val.startsWith("0x")) {
            return Integer.parseInt(val.substring(2), 16);
        } else {
            return radix == 16 && val != null && val.startsWith("-0x") ? Integer.parseInt("-" + val.substring(3), 16) : Integer.parseInt(val, radix);
        }
    }

    public long getLong(String key) {
        return this.getLong(key, 10);
    }

    public long getLong(String key, int radix) {
        if (!this.contains(key)) {
            throw new NoSuchElementException(key);
        } else {
            return this.getLong(key, radix, 0L);
        }
    }

    public long getLong(String key, int radix, long dflt) {
        String val = this.getLast(key);
        if (val == null) {
            return dflt;
        } else if (radix == 16 && val != null && val.startsWith("0x")) {
            return Long.parseLong(val.substring(2), 16);
        } else {
            return radix == 16 && val != null && val.startsWith("-0x") ? Long.parseLong("-" + val.substring(3), 16) : Long.parseLong(val, radix);
        }
    }

    public double getDouble(String key) {
        return Double.parseDouble(this.getLast(key));
    }

    public double getDouble(String key, double dflt) {
        String val = this.getLast(key);
        return val == null ? dflt : Double.parseDouble(val);
    }

    public boolean ok() {
        return this.ok;
    }

    public List<String> optionKeys() {
        return this.keyList;
    }

    public List<String> optionValues() {
        return this.valueList;
    }

    public Iterator<String> iterator() {
        return (new HashSet(this.keyList)).iterator();
    }

    private void createRawOptionLists(String[] args, List<OptionSpec> optionSpecs) {
        this.keyList.clear();
        this.valueList.clear();

        int i;
        for(i = 0; i < args.length; ++i) {
            Matcher m = ARGUMENT.matcher(args[i]);
            if (!m.matches()) {
                break;
            }

            if (m.group(1) != null) {
                ++i;
                break;
            }

            if (m.group(2) != null) {
                this.keyList.add(m.group(2));
                if (m.group(3) == null) {
                    this.valueList.add("");
                } else {
                    this.valueList.add(m.group(3));
                }
            } else if (shortOptionWithArg(m.group(4), optionSpecs)) {
                this.keyList.add(m.group(4).substring(0, 2));
                if (m.group(4).length() > 2) {
                    this.valueList.add(m.group(4).substring(2));
                } else {
                    ++i;
                    if (i == args.length) {
                        this.ok = false;
                        this.valueList.add("");
                    } else {
                        this.valueList.add(args[i]);
                    }
                }
            } else {
                for(int k = 1; k < m.group(4).length(); ++k) {
                    String key = "-" + m.group(4).charAt(k);
                    this.keyList.add(key);
                    if (shortOptionWithArg(key, optionSpecs)) {
                        this.ok = false;
                    }

                    this.valueList.add("");
                }
            }
        }

        while(i < args.length) {
            this.keyList.add("--");
            this.valueList.add(args[i]);
            ++i;
        }

    }

    private void checkOptions(ArrayList<OptionSpec> specs) {
        label32:
        for(int i = 0; i < this.keyList.size(); ++i) {
            Iterator i$ = specs.iterator();

            OptionSpec spec;
            do {
                if (!i$.hasNext()) {
                    this.ok = false;
                    continue label32;
                }

                spec = (OptionSpec)i$.next();
            } while(!spec.matches((String)this.keyList.get(i)));

            this.ok &= spec.validValue((String)this.valueList.get(i));
        }

        OptionSpec spec;
        for(Iterator i$ = specs.iterator(); i$.hasNext(); this.ok &= spec.hasMinValues()) {
            spec = (OptionSpec)i$.next();
        }

    }

    private static boolean shortOptionWithArg(String opt, List<OptionSpec> specs) {
        if (!opt.startsWith("--") && opt.startsWith("-") && opt.length() >= 2) {
            opt = opt.substring(0, 2);

            for(int i = 0; i < specs.size(); ++i) {
                if (((OptionSpec)specs.get(i)).matches(opt)) {
                    return ((OptionSpec)specs.get(i)).hasArgument();
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private static class OptionSpec {
        private String key;
        private String valuePattern;
        private OptionSpec primary;
        private int min;
        private int max;
        private int groupCount;
        private int count;

        private OptionSpec() {
        }

        static ArrayList<OptionSpec> parse(String opt) {
            ArrayList<OptionSpec> result = new ArrayList();
            HashMap<String, OptionSpec> labels = new HashMap();
            String[] opts = opt.trim().split("\\s+");

            for(int i = 0; i < opts.length; ++i) {
                if (!opts[i].equals("")) {
                    Matcher m = CommandArgs.OPTION_PATTERN.matcher(opts[i]);
                    if (!m.matches()) {
                        throw new IllegalArgumentException("bad option specifier: " + opts[i]);
                    }

                    OptionSpec spec = new OptionSpec();
                    spec.key = m.group(1);
                    spec.valuePattern = "".equals(m.group(2)) ? ".+" : m.group(2);
                    spec.primary = spec;
                    if (m.group(7) != null) {
                        spec.primary = (OptionSpec)labels.get(m.group(7));
                        if (spec.primary == null) {
                            throw new IllegalArgumentException("undefined label: " + m.group(7));
                        }
                    } else if (m.group(6) != null) {
                        if (labels.containsKey(m.group(6))) {
                            throw new IllegalArgumentException("multiply defined label: " + m.group(6));
                        }

                        labels.put(m.group(6), spec);
                    }

                    if (m.group(3) != null) {
                        spec.min = Integer.parseInt(m.group(3));
                        if (m.group(4) == null) {
                            spec.max = spec.min;
                        } else if (m.group(5).equals("")) {
                            spec.max = Integer.MAX_VALUE;
                        } else {
                            spec.max = Integer.parseInt(m.group(5));
                        }
                    } else {
                        spec.min = 0;
                        spec.max = Integer.MAX_VALUE;
                    }

                    spec.count = 0;
                    result.add(spec);
                    if (spec.key.equals("--") && i != opts.length - 1) {
                        throw new IllegalArgumentException("junk at end of option string");
                    }
                }
            }

            return result;
        }

        boolean matches(String key) {
            return key.equals(this.key);
        }

        boolean validValue(String val) {
            if (this.valuePattern == null) {
                if (!val.equals("")) {
                    return false;
                }
            } else if (!Pattern.matches(this.valuePattern, val)) {
                return false;
            }

            ++this.count;
            ++this.primary.groupCount;
            return this.count == this.primary.groupCount && this.count <= this.primary.max;
        }

        boolean hasMinValues() {
            return this.primary.count >= this.primary.min;
        }

        boolean hasArgument() {
            return this.valuePattern != null;
        }

        String key() {
            return this.key;
        }
    }
}

