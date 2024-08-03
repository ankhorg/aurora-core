package org.inksnow.core.impl.util;

import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

@SuppressWarnings({"rawtypes", "WeakerAccess"})
public final class TypeTokenUtil {

    private TypeTokenUtil() {
    }

    public static Class<?> getGenericParam(final TypeToken<?> token, final int typeIndex) {
        return (Class) ((ParameterizedType) token.getType()).getActualTypeArguments()[typeIndex];
    }

    /**
     * Throw an exception if the passed type is raw
     *
     * @param input input type
     * @return type, passed through
     */
    public static Type requireCompleteGenerics(final Type input) {
        if (GenericTypeReflector.isMissingTypeParameters(input)) {
            throw new IllegalArgumentException("Provided type " + input + " is a raw type, which is not accepted.");
        }
        return input;
    }

    // Type comparison functions
    // Unlike normal GenericTypeReflector#isSupertypeOf, these functions treat generic parameters as covariant.

    /**
     * Given a known declared subtype, determine the value of a specific type parameter in a supertype.
     *
     * @param sub       subtype
     * @param superType superclass to resolve
     * @param idx       Parameter index to resolve
     * @return type argument
     */
    public static Type typeArgumentFromSupertype(final Type sub, final Class<?> superType, final int idx) {
        final Type calculatedSuper = GenericTypeReflector.getExactSuperType(sub, superType);
        if (!(calculatedSuper instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Calculated supertype " + calculatedSuper + " of original type " + sub + " is not parameterized");
        }
        final Type[] parameters = ((ParameterizedType) calculatedSuper).getActualTypeArguments();
        if (parameters.length < idx) {
            throw new IllegalArgumentException("Expected calculated supertype " + calculatedSuper + " of type " + sub + " to have at least " + idx
                    + "parameter(s), but got " + parameters.length);
        }
        return parameters[idx];
    }

    public static boolean isAssignable(final TypeToken<?> type, final TypeToken<?> toType) {
        return TypeTokenUtil.isAssignable(type.getType(), toType.getType());
    }

    public static boolean isAssignable(final Type type, final Type toType) {
        return TypeTokenUtil.isAssignable(type, toType, null, 0);
    }

    public static boolean isArray(final Type input) {
        if (input instanceof Class<?>) {
            return ((Class<?>) input).isArray();
        } else if (input instanceof ParameterizedType) {
            return isArray(((ParameterizedType) input).getRawType());
        } else {
            return input instanceof GenericArrayType;
        }
    }

    private static boolean isAssignable(final Type type, final Type toType, final @Nullable Type parent, final int index) {
        if (type.equals(toType)) {
            return true;
        }
        if (toType instanceof Class) {
            return TypeTokenUtil.isAssignable(type, (Class<?>) toType, parent, index);
        }
        if (toType instanceof ParameterizedType) {
            return TypeTokenUtil.isAssignable(type, (ParameterizedType) toType, parent, index);
        }
        if (toType instanceof TypeVariable) {
            return TypeTokenUtil.isAssignable(type, (TypeVariable) toType, parent, index);
        }
        if (toType instanceof WildcardType) {
            return TypeTokenUtil.isAssignable(type, (WildcardType) toType, parent, index);
        }
        if (toType instanceof GenericArrayType) {
            return TypeTokenUtil.isAssignable(type, (GenericArrayType) toType, parent, index);
        }
        throw new IllegalStateException("Unsupported type: " + type);
    }

    private static boolean isAssignable(Type type, Class<?> toType, @Nullable Type parent, int index) {
        if (type instanceof Class) {
            final Class<?> other = (Class<?>) type;
            final Class<?> toEnclosing = toType.getEnclosingClass();
            if (toEnclosing != null && !Modifier.isStatic(toType.getModifiers())) {
                final Class<?> otherEnclosing = other.getEnclosingClass();
                if (otherEnclosing == null || !TypeTokenUtil.isAssignable(otherEnclosing, toEnclosing, null, 0)) {
                    return false;
                }
            }
            return toType.isAssignableFrom(other);
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType other = (ParameterizedType) type;
            final Class<?> toEnclosing = toType.getEnclosingClass();
            if (toEnclosing != null && !Modifier.isStatic(toType.getModifiers())) {
                final Type otherEnclosing = other.getOwnerType();
                if (otherEnclosing == null || !TypeTokenUtil.isAssignable(otherEnclosing, toEnclosing, null, 0)) {
                    return false;
                }
            }
            return toType.isAssignableFrom((Class<?>) other.getRawType());
        }
        if (type instanceof TypeVariable) {
            final TypeVariable other = (TypeVariable) type;
            return TypeTokenUtil.allSupertypes(toType, other.getBounds());
        }
        if (type instanceof WildcardType) {
            final WildcardType other = (WildcardType) type;
            return TypeTokenUtil.allWildcardSupertypes(toType, other.getUpperBounds(), parent, index) &&
                    TypeTokenUtil.allAssignable(toType, other.getLowerBounds());
        }
        if (type instanceof GenericArrayType) {
            final GenericArrayType other = (GenericArrayType) type;
            return toType.equals(Object.class) || (toType.isArray() &&
                    TypeTokenUtil.isAssignable(other.getGenericComponentType(), toType.getComponentType(), parent, index));
        }
        throw new IllegalStateException("Unsupported type: " + type);
    }

    @SuppressWarnings("rawtypes")
    private static boolean isAssignable(Type type, ParameterizedType toType, @Nullable Type parent, int index) {
        if (type instanceof Class) {
            final Class<?> otherRaw = (Class<?>) type;
            final Class<?> toRaw = (Class<?>) toType.getRawType();
            if (!toRaw.isAssignableFrom(otherRaw)) {
                return false;
            }
            final Type toEnclosing = toType.getOwnerType();
            if (toEnclosing != null && !Modifier.isStatic(toRaw.getModifiers())) {
                final Class<?> otherEnclosing = otherRaw.getEnclosingClass();
                if (otherEnclosing == null || !TypeTokenUtil.isAssignable(otherEnclosing, toEnclosing, null, 0)) {
                    return false;
                }
            }
            // Check if the default generic parameters match the parameters
            // of the parameterized type
            final Type[] toTypes = toType.getActualTypeArguments();
            final Type[] types;
            if (otherRaw.equals(toRaw)) {
                types = otherRaw.getTypeParameters();
            } else {
                // Get the type parameters based on the super class
                final ParameterizedType other = (ParameterizedType) GenericTypeReflector.getExactSuperType(type, toRaw);
                types = other.getActualTypeArguments();
            }
            if (types.length != toTypes.length) {
                return false;
            }
            for (int i = 0; i < types.length; i++) {
                if (!TypeTokenUtil.isAssignable(types[i], toTypes[i], type, i)) {
                    return false;
                }
            }
            return true;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType other = (ParameterizedType) type;
            final Class<?> otherRaw = (Class<?>) other.getRawType();
            final Class<?> toRaw = (Class<?>) toType.getRawType();
            if (!toRaw.isAssignableFrom(otherRaw)) {
                return false;
            }
            final Type toEnclosing = toType.getOwnerType();
            if (toEnclosing != null && !Modifier.isStatic(toRaw.getModifiers())) {
                final Type otherEnclosing = other.getOwnerType();
                if (otherEnclosing == null || !TypeTokenUtil.isAssignable(otherEnclosing, toEnclosing, null, 0)) {
                    return false;
                }
            }
            final Type[] types;
            if (otherRaw.equals(toRaw)) {
                types = other.getActualTypeArguments();
            } else {
                // Get the type parameters based on the super class
                other = (ParameterizedType) GenericTypeReflector.getExactSuperType(other, toRaw);
                types = other.getActualTypeArguments();
            }
            final Type[] toTypes = toType.getActualTypeArguments();
            if (types.length != toTypes.length) {
                return false;
            }
            for (int i = 0; i < types.length; i++) {
                if (!TypeTokenUtil.isAssignable(types[i], toTypes[i], other, i)) {
                    return false;
                }
            }
            return true;
        }
        if (type instanceof TypeVariable) {
            final TypeVariable other = (TypeVariable) type;
            return TypeTokenUtil.allSupertypes(toType, other.getBounds());
        }
        if (type instanceof WildcardType) {
            final WildcardType other = (WildcardType) type;
            return TypeTokenUtil.allWildcardSupertypes(toType, other.getUpperBounds(), parent, index) &&
                    TypeTokenUtil.allAssignable(toType, other.getLowerBounds());
        }
        if (type instanceof GenericArrayType) {
            final GenericArrayType other = (GenericArrayType) type;
            final Class<?> rawType = (Class<?>) toType.getRawType();
            return rawType.equals(Object.class) || (rawType.isArray() &&
                    TypeTokenUtil.isAssignable(other.getGenericComponentType(), rawType.getComponentType(), parent, index));
        }
        throw new IllegalStateException("Unsupported type: " + type);
    }

    private static boolean isAssignable(final Type type, final TypeVariable toType, final @Nullable Type parent, final int index) {
        return TypeTokenUtil.allAssignable(type, toType.getBounds());
    }

    private static boolean isAssignable(final Type type, final WildcardType toType, final @Nullable Type parent, final int index) {
        return TypeTokenUtil.allWildcardAssignable(type, toType.getUpperBounds(), parent, index) &&
                TypeTokenUtil.allSupertypes(type, toType.getLowerBounds());
    }

    private static boolean isAssignable(final Type type, final GenericArrayType toType, final @Nullable Type parent, final int index) {
        if (type instanceof Class) {
            final Class<?> other = (Class<?>) type;
            return other.isArray() && TypeTokenUtil.isAssignable(other.getComponentType(), toType.getGenericComponentType(), parent, index);
        }
        if (type instanceof ParameterizedType) {
            final ParameterizedType other = (ParameterizedType) type;
            final Class<?> rawType = (Class<?>) other.getRawType();
            return rawType.isArray() && TypeTokenUtil.isAssignable(rawType.getComponentType(), toType.getGenericComponentType(), parent, index);
        }
        if (type instanceof TypeVariable) {
            final TypeVariable other = (TypeVariable) type;
            return TypeTokenUtil.allSupertypes(toType, other.getBounds());
        }
        if (type instanceof WildcardType) {
            final WildcardType other = (WildcardType) type;
            return TypeTokenUtil.allWildcardSupertypes(toType, other.getUpperBounds(), parent, index) &&
                    TypeTokenUtil.allAssignable(toType, other.getLowerBounds());
        }
        if (type instanceof GenericArrayType) {
            final GenericArrayType other = (GenericArrayType) type;
            return TypeTokenUtil.isAssignable(other.getGenericComponentType(), toType.getGenericComponentType(), parent, index);
        }
        throw new IllegalStateException("Unsupported type: " + type);
    }

    private static Type[] processBounds(Type[] bounds, @Nullable Type parent, int index) {
        if (bounds.length == 0 ||
                (bounds.length == 1 && bounds[0].equals(Object.class))) {
            Class<?> theClass = null;
            if (parent instanceof Class) {
                theClass = (Class<?>) parent;
            } else if (parent instanceof ParameterizedType) {
                theClass = (Class<?>) ((ParameterizedType) parent).getRawType();
            }
            if (theClass != null) {
                final TypeVariable[] typeVariables = theClass.getTypeParameters();
                bounds = typeVariables[index].getBounds();
                // Strip the new bounds down
                for (int i = 0; i < bounds.length; i++) {
                    if (bounds[i] instanceof TypeVariable ||
                            bounds[i] instanceof WildcardType) {
                        bounds[i] = Object.class;
                    } else if (bounds[i] instanceof ParameterizedType) {
                        bounds[i] = ((ParameterizedType) bounds[i]).getRawType();
                    } else if (bounds[i] instanceof GenericArrayType) {
                        final Type component = ((GenericArrayType) bounds[i]).getGenericComponentType();
                        final Class<?> componentClass;
                        if (component instanceof Class) {
                            componentClass = (Class<?>) component;
                        } else if (component instanceof ParameterizedType) { // Is this even possible?
                            componentClass = (Class<?>) ((ParameterizedType) component).getRawType();
                        } else {
                            componentClass = Object.class;
                        }
                        bounds[i] = componentClass == Object.class ? Object[].class :
                                Array.newInstance(componentClass, 0).getClass(); // Get the array class
                    }
                }
            }
        }
        return bounds;
    }

    private static boolean allWildcardSupertypes(Type type, Type[] bounds, @Nullable Type parent, int index) {
        return TypeTokenUtil.allSupertypes(type, TypeTokenUtil.processBounds(bounds, parent, index));
    }

    private static boolean allWildcardAssignable(Type type, Type[] bounds, @Nullable Type parent, int index) {
        return TypeTokenUtil.allAssignable(type, TypeTokenUtil.processBounds(bounds, parent, index));
    }

    private static boolean allAssignable(Type type, Type[] bounds) {
        for (Type toType : bounds) {
            // Skip the Object class
            if (!TypeTokenUtil.isAssignable(type, toType, null, 0)) {
                return false;
            }
        }
        return true;
    }

    private static boolean allSupertypes(Type type, Type[] bounds) {
        for (Type toType : bounds) {
            // Skip the Object class
            if (!TypeTokenUtil.isAssignable(toType, type, null, 0)) {
                return false;
            }
        }
        return true;
    }
}
